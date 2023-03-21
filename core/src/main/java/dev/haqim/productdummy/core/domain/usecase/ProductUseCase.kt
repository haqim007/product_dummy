package dev.haqim.productdummy.core.domain.usecase

import androidx.paging.PagingData
import dev.haqim.productdummy.core.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductUseCase {
    fun getProducts(): Flow<PagingData<Product>>
    fun getFavoriteProducts(): Flow<PagingData<Product>>
    suspend fun toggleFavorite(product: Product)
    suspend fun removeFavorite(product: Product)
    fun getProduct(product: Product): Flow<Product?>
}