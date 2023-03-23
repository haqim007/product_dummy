package dev.haqim.productdummy.favorites

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
class FavoritesViewModel constructor(
    val useCase: ProductUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState = _uiState.stateIn(
        viewModelScope, SharingStarted.Eagerly, FavoritesUiState()
    )
    private val actionStateFlow = MutableSharedFlow<FavoritesUiAction>(
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val pagingDataFlow: Flow<PagingData<Product>>
    init {
        actionStateFlow.updateStates().launchIn(viewModelScope)

        val getProductsAction = actionStateFlow
            .filterIsInstance<FavoritesUiAction.GetFavoriteProducts>()
            .distinctUntilChanged()
            .onStart {
                emit(FavoritesUiAction.GetFavoriteProducts)
            }

        /*
        * .flatMapLatest trigger to start collecting upstream flow immediately
        * */
        pagingDataFlow = getProductsAction
            .flatMapLatest { onGetFavoriteProducts() }
            .cachedIn(viewModelScope)
    }

    fun processAction(action: FavoritesUiAction): Boolean {
        return actionStateFlow.tryEmit(action)
    }

    private fun MutableSharedFlow<FavoritesUiAction>.updateStates() = onEach {
        when(it){ 
            is FavoritesUiAction.RemoveFavorite -> {
                viewModelScope.launch {
                    useCase.removeFavorite(it.product).collect{
                        _uiState.update { state ->
                            state.copy(
                                addToFavoriteResult = it
                            )
                        }
                    }
                }
            }
            is FavoritesUiAction.OpenProduct -> {
                _uiState.update { state -> 
                    state.copy(productToOpen = it.product)
                }
            }
            is FavoritesUiAction.AfterOpenProduct -> {
                _uiState.update { state ->
                    state.copy(productToOpen = null)
                }
            }
            is FavoritesUiAction.GetFavoriteProducts -> {
                onGetFavoriteProducts()
            }
        }
    }
    
    private fun onGetFavoriteProducts(): Flow<PagingData<Product>> {
        return useCase.getFavoriteProducts()
    }

}

data class FavoritesUiState(
    val productToOpen: Product? = null,
    val addToFavoriteResult: Resource<Boolean> = Resource.Idle()
)

sealed class FavoritesUiAction{
    object GetFavoriteProducts: FavoritesUiAction()
    data class RemoveFavorite(val product: Product): FavoritesUiAction()
    data class OpenProduct(val product: Product): FavoritesUiAction()
    object  AfterOpenProduct: FavoritesUiAction()
}