package dev.haqim.productdummy.domain.repository

import androidx.paging.PagingData
import dev.haqim.productdummy.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface IProductRepository {
    fun getProducts(): Flow<PagingData<Product>>
    fun getFavoriteProducts(): Flow<PagingData<Product>>
    suspend fun toggleFavorite(product: Product)
    suspend fun removeFavorite(product: Product)
    fun getProduct(product: Product): Flow<Product?>
}