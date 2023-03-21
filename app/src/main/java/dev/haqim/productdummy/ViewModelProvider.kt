package dev.haqim.productdummy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.haqim.productdummy.domain.usecase.ProductUseCase
import dev.haqim.productdummy.ui.detail.DetailProductViewModel
import dev.haqim.productdummy.ui.favorites.FavoritesViewModel
import dev.haqim.productdummy.ui.list.ProductListViewModel

//class ViewModelProvider(
//    private val useCase: ProductUseCase
//): ViewModelProvider.NewInstanceFactory() {
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return when {
//            modelClass.isAssignableFrom(ProductListViewModel::class.java) -> {
//                ProductListViewModel(useCase) as T
//            }
//            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> {
//                FavoritesViewModel(useCase) as T
//            }
//            modelClass.isAssignableFrom(DetailProductViewModel::class.java) -> {
//                DetailProductViewModel(useCase) as T
//            }
//            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
//        }
//    }
//}