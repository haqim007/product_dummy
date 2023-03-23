package dev.haqim.productdummy.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.haqim.productdummy.core.data.mechanism.Resource
import dev.haqim.productdummy.core.domain.model.Product
import dev.haqim.productdummy.core.domain.usecase.ProductUseCase
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetailProductViewModel (private val useCase: ProductUseCase): ViewModel() {
    private val _uiState = MutableStateFlow(DetailProductUiState())
    val uiState = _uiState.stateIn(
        viewModelScope, SharingStarted.Eagerly, DetailProductUiState()
    )
    private val actionStateFlow = MutableSharedFlow<DetailProductUiAction>(
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST)
    init {
        actionStateFlow.updateStates().launchIn(viewModelScope)
    }

    fun processAction(action: DetailProductUiAction): Boolean {
        return actionStateFlow.tryEmit(action)
    }

    private fun MutableSharedFlow<DetailProductUiAction>.updateStates() = onEach {
        when(it){
            is DetailProductUiAction.ToggleFavorite -> {
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
            is DetailProductUiAction.GetProduct -> {
                viewModelScope.launch {
                    onGetProduct(it.product).collect{
                        _uiState.update { state ->
                            state.copy(
                                product = it
                            )
                        }
                    }
                }
            }
        }
    }
    
    private fun onGetProduct(product: Product): Flow<Product?> {
        return useCase.getProduct(product)
    }

}

data class DetailProductUiState(
    val product: Product? = null,
    val addToFavoriteResult: Resource<Boolean> = Resource.Idle()
)

sealed class DetailProductUiAction{
    data class GetProduct(val product: Product): DetailProductUiAction()
    data class ToggleFavorite(val product: Product): DetailProductUiAction()
}