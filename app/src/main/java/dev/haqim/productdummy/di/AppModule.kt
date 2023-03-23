package dev.haqim.productdummy.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.haqim.productdummy.core.domain.usecase.ProductInteractor
import dev.haqim.productdummy.core.domain.usecase.ProductUseCase


@Module
@InstallIn(ApplicationComponent::class)
abstract class AppModule {

    @Binds
    abstract fun provideProductUseCase(
        productInteractor: ProductInteractor
    ): ProductUseCase
}
