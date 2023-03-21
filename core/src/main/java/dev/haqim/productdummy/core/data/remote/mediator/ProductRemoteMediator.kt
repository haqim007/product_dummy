package dev.haqim.productdummy.core.data.remote.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dev.haqim.productdummy.core.data.local.LocalDataSource
import dev.haqim.productdummy.core.data.local.entity.ProductWithFavoriteEntity
import dev.haqim.productdummy.core.data.local.entity.RemoteKeys
import dev.haqim.productdummy.core.data.remote.RemoteDataSource
import dev.haqim.productdummy.core.data.mapper.toEntity

@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val localDataSource: dev.haqim.productdummy.core.data.local.LocalDataSource,
    private val remoteDataSource: RemoteDataSource
): RemoteMediator<Int, dev.haqim.productdummy.core.data.local.entity.ProductWithFavoriteEntity>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, dev.haqim.productdummy.core.data.local.entity.ProductWithFavoriteEntity>): MediatorResult {

        val page = when(loadType){
            LoadType.REFRESH -> {
                val remoteKeys: dev.haqim.productdummy.core.data.local.entity.RemoteKeys? = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys: dev.haqim.productdummy.core.data.local.entity.RemoteKeys? = getRemoteKeyForFirstItem(state)
                val prevKey: Int = remoteKeys?.prevKey ?:
                return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys: dev.haqim.productdummy.core.data.local.entity.RemoteKeys? = getRemoteKeyForLastItem(state)
                val nextKey: Int = remoteKeys?.nextKey ?:
                return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        return try {
            val skip = (page - 1) * state.config.pageSize
            val response = remoteDataSource.getProducts(state.config.pageSize, skip)
            val endOfPaginationReached = response.getOrNull()?.products?.isEmpty() ?: true

            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val products = response.getOrNull()?.products
            val keys = products?.map {
                dev.haqim.productdummy.core.data.local.entity.RemoteKeys(
                    id = it.id.toString(),
                    prevKey,
                    nextKey
                )
            } ?: listOf()

            localDataSource.insertKeysAndProducts(
                keys,
                products.toEntity(),
                loadType == LoadType.REFRESH
            )

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        }catch (e: Exception){
            MediatorResult.Error(e)
        }

    }


    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, dev.haqim.productdummy.core.data.local.entity.ProductWithFavoriteEntity>): dev.haqim.productdummy.core.data.local.entity.RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.product?.id?.let { id ->
                localDataSource.getRemoteKeysById(id.toString())
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, dev.haqim.productdummy.core.data.local.entity.ProductWithFavoriteEntity>): dev.haqim.productdummy.core.data.local.entity.RemoteKeys?{
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { data ->
                localDataSource.getRemoteKeysById(data.product.id.toString())
            }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, dev.haqim.productdummy.core.data.local.entity.ProductWithFavoriteEntity>): dev.haqim.productdummy.core.data.local.entity.RemoteKeys?{
        return state.pages.lastOrNull{it.data.isNotEmpty()}?.data?.lastOrNull()?.let { data ->
            localDataSource.getRemoteKeysById(data.product.id.toString())
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}