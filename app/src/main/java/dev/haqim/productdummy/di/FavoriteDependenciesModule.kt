package dev.haqim.productdummy.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.haqim.productdummy.core.domain.usecase.ProductUseCase


@EntryPoint
@InstallIn(ApplicationComponent::class)
interface FavoriteModuleDependencies {
    fun productUseCase() : ProductUseCase
}