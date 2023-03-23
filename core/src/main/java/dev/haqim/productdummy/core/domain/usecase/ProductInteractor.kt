package dev.haqim.productdummy.core.domain.usecase

import androidx.paging.PagingData
import dev.haqim.productdummy.core.data.mechanism.Resource
import dev.haqim.productdummy.core.domain.model.Product
import dev.haqim.productdummy.core.domain.repository.IProductRepository
import kotlinx.coroutines.flow.Flow

class ProductInteractor constructor(
    private val repository: IProductRepository
): ProductUseCase {
    override fun getProducts(): Flow<PagingData<Product>> {
        return repository.getProducts()
    }

    override fun getFavoriteProducts(): Flow<PagingData<Product>> {
        return repository.getFavoriteProducts()
    }

    override suspend fun toggleFavorite(product: Product): Flow<Resource<Boolean>> {
        return repository.toggleFavorite(product)
    }

    override suspend fun removeFavorite(product: Product): Flow<Resource<Boolean>> {
        return repository.removeFavorite(product)
    }

    override fun getProduct(product: Product): Flow<Product?> {
        return repository.getProduct(product)
    }
}