package dev.haqim.productdummy.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dev.haqim.productdummy.core.domain.usecase.ProductInteractor
import dev.haqim.productdummy.core.domain.usecase.ProductUseCase


@Module
@InstallIn(ViewModelComponent::class)
abstract class AppModule {
    
    @Binds
    @ViewModelScoped
    abstract fun provideProductUseCase(
        productInteractor: ProductInteractor
    ): ProductUseCase
}

