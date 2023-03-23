package dev.haqim.productdummy.core.domain.repository

import androidx.paging.PagingData
import dev.haqim.productdummy.core.data.mechanism.Resource
import dev.haqim.productdummy.core.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface IProductRepository {
    fun getProducts(): Flow<PagingData<Product>>
    fun getFavoriteProducts(): Flow<PagingData<Product>>
    suspend fun toggleFavorite(product: Product): Flow<Resource<Boolean>>
    suspend fun removeFavorite(product: Product): Flow<Resource<Boolean>>
    fun getProduct(product: Product): Flow<Product?>
}