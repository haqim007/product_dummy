package dev.haqim.productdummy.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.haqim.productdummy.core.domain.usecase.ProductUseCase
import javax.inject.Inject


class FavoritesViewModelFactory @Inject constructor(private val productUseCase: ProductUseCase) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> {
                FavoritesViewModel(productUseCase) as T
            }
            else -> throw Throwable("Unkwnown Viewmodel class: " + modelClass.name)
        }
    
}