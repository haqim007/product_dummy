package dev.haqim.productdummy.di

import dev.haqim.productdummy.core.domain.usecase.ProductInteractor
import dev.haqim.productdummy.core.domain.usecase.ProductUseCase
import dev.haqim.productdummy.feature.detail.DetailProductViewModel
import dev.haqim.productdummy.feature.list.ProductListViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val useCaseModule = module {
    factory <ProductUseCase> { ProductInteractor(get()) }
}

val viewModelModule = module {
    viewModel { ProductListViewModel(get()) }
    viewModel { DetailProductViewModel(get()) }
}