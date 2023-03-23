package dev.haqim.productdummy.core.data.repository

import androidx.paging.*
import dev.haqim.productdummy.core.data.mapper.toModel
import dev.haqim.productdummy.core.data.mapper.toProductFavoriteEntity
import dev.haqim.productdummy.core.data.mechanism.Resource
import dev.haqim.productdummy.core.data.remote.mediator.ProductRemoteMediator
import dev.haqim.productdummy.core.domain.model.Product
import dev.haqim.productdummy.core.domain.repository.IProductRepository
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalPagingApi::class)
class ProductRepository constructor(
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

    override suspend fun toggleFavorite(product: Product): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())
            try {
                if(product.isFavorite){
                    localDataSource.removeFavoriteProduct(product.id)
                }else{
                    localDataSource.insertFavoriteProduct(product.toProductFavoriteEntity())
                }
                emit(Resource.Success(true))
            }catch (e: Exception){
                emit(Resource.Error(e.localizedMessage, false))
            }
        }.onCompletion {
            emit(Resource.Idle())
        }
    }

    override suspend fun removeFavorite(product: Product): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())
            try {
                localDataSource.removeFavoriteProduct(product.id)
                emit(Resource.Success(true))
            }catch (e: Exception){
                emit(Resource.Error(e.localizedMessage, false))
            }
        }.onCompletion {
            emit(Resource.Idle())
        }
    }

    override fun getProduct(product: Product): Flow<Product?> {
        return localDataSource.getProductById(product.id).map { 
            it?.toModel()
        }
    }
}