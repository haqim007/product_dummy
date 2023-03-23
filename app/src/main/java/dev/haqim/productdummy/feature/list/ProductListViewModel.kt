package dev.haqim.productdummy.feature.list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.haqim.productdummy.core.data.mechanism.Resource
import dev.haqim.productdummy.core.domain.model.Product
import dev.haqim.productdummy.core.domain.usecase.ProductUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModel @ViewModelInject constructor (private val useCase: ProductUseCase): ViewModel() {
    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState = _uiState.stateIn(
        viewModelScope, SharingStarted.Eagerly, ProductListUiState()
    )
    private val actionStateFlow = MutableSharedFlow<ProductListUiAction>(
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val pagingDataFlow: Flow<PagingData<Product>>
    init {
        actionStateFlow.updateStates().launchIn(viewModelScope)

        val getProductsAction = actionStateFlow
            .filterIsInstance<ProductListUiAction.GetProducts>()
            .distinctUntilChanged()
            .onStart {
                emit(ProductListUiAction.GetProducts)
            }

        /*
        * .flatMapLatest trigger to start collecting upstream flow immediately
        * */
        pagingDataFlow = getProductsAction
            .flatMapLatest { onGetProducts() }
            .cachedIn(viewModelScope)
    }

    fun processAction(action: ProductListUiAction): Boolean {
        return actionStateFlow.tryEmit(action)
    }

    private fun MutableSharedFlow<ProductListUiAction>.updateStates() = onEach {
        when(it){ 
            is ProductListUiAction.ToggleFavorite -> {
                viewModelScope.launch {
                    useCase.toggleFavorite(it.product).collect{
                        _uiState.update { state -> 
                            state.copy(
                                addToFavoriteResult = it
                            )
                        }
                    }
                }
            }
            is ProductListUiAction.OpenProduct -> {
                _uiState.update { state -> 
                    state.copy(productToOpen = it.product)
                }
            }
            is ProductListUiAction.AfterOpenProduct -> {
                _uiState.update { state ->
                    state.copy(productToOpen = null)
                }
            }
            is ProductListUiAction.GetProducts -> {
                onGetProducts()
            }
            is ProductListUiAction.NavigateToFavorite -> {
                _uiState.update { state ->
                    state.copy(
                        navigateToFavorite = !state.navigateToFavorite
                    )
                }
            }
        }
    }
    
    private fun onGetProducts(): Flow<PagingData<Product>> {
        return useCase.getProducts()
    }

}

data class ProductListUiState(
    val productToOpen: Product? = null,
    val navigateToFavorite: Boolean = false,
    val addToFavoriteResult: Resource<Boolean> = Resource.Idle()
)

sealed class ProductListUiAction{
    object GetProducts: ProductListUiAction()
    data class ToggleFavorite(val product: Product): ProductListUiAction()
    data class OpenProduct(val product: Product): ProductListUiAction()
    object  AfterOpenProduct: ProductListUiAction()
    object NavigateToFavorite: ProductListUiAction()
}