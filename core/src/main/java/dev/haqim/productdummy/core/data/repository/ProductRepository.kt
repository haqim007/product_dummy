package dev.haqim.productdummy.core.data.repository

import androidx.paging.*
import dev.haqim.productdummy.core.data.local.LocalDataSource
import dev.haqim.productdummy.core.data.mapper.toModel
import dev.haqim.productdummy.core.data.mapper.toProductFavoriteEntity
import dev.haqim.productdummy.core.data.remote.mediator.ProductRemoteMediator
import dev.haqim.productdummy.core.domain.model.Product
import dev.haqim.productdummy.core.domain.repository.IProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ProductRepository @Inject constructor(
    private val remoteMediator: ProductRemoteMediator,
    private val localDataSource: dev.haqim.productdummy.core.data.local.LocalDataSource
): IProductRepository {
    
    override fun getProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = {
                localDataSource.getAllProducts()
            }
        ).flow.map { pagingData ->
            pagingData.map { productEntity ->
                productEntity.toModel()
            }
        }
    }

    override fun getFavoriteProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = {
                localDataSource.getAllFavoriteProducts()
            }
        ).flow.map { pagingData ->
            pagingData.map { productEntity ->
                productEntity.toModel()
            }
        }
    }

    override suspend fun toggleFavorite(product: Product){
        if(product.isFavorite){
            localDataSource.removeFavoriteProduct(product.id)
        }else{
            localDataSource.insertFavoriteProduct(product.toProductFavoriteEntity())
        }
    }

    override suspend fun removeFavorite(product: Product) {
        localDataSource.removeFavoriteProduct(product.id)
    }

    override fun getProduct(product: Product): Flow<Product?> {
        return localDataSource.getProductById(product.id).map { 
            it?.toModel()
        }
    }
}