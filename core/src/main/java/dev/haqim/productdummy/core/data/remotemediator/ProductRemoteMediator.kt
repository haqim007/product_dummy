package dev.haqim.productdummy.core.data.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dev.haqim.productdummy.core.data.local.LocalDataSource
import dev.haqim.productdummy.core.data.local.entity.ProductWithFavoriteEntity
import dev.haqim.productdummy.core.data.local.entity.RemoteKeys
import dev.haqim.productdummy.core.data.mapper.toEntity
import dev.haqim.productdummy.core.data.remote.RemoteDataSource

@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
): RemoteMediator<Int, ProductWithFavoriteEntity>() {
    override suspend fun  load(loadType: LoadType, state: PagingState<Int, ProductWithFavoriteEntity>): MediatorResult {

        val page = when(loadType){
            LoadType.REFRESH -> {
                val remoteKeys: RemoteKeys? = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys: RemoteKeys? = getRemoteKeyForFirstItem(state)
                val prevKey: Int = remoteKeys?.prevKey ?:
                return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys: RemoteKeys? = getRemoteKeyForLastItem(state)
                val nextKey: Int = remoteKeys?.nextKey ?:
                return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        return try {
            val skip = (page - 1) * state.config.pageSize
            val response = remoteDataSource.getProducts(state.config.pageSize, skip).getOrThrow()
            val endOfPaginationReached = response.products.isEmpty()

            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val products = response.products
            val keys = products.map {
                RemoteKeys(
                    id = it.id.toString(),
                    prevKey,
                    nextKey
                )
            }

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


    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ProductWithFavoriteEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.product?.id?.let { id ->
                localDataSource.getRemoteKeysById(id.toString())
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ProductWithFavoriteEntity>): RemoteKeys?{
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { data ->
                localDataSource.getRemoteKeysById(data.product.id.toString())
            }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ProductWithFavoriteEntity>): RemoteKeys?{
        return state.pages.lastOrNull{it.data.isNotEmpty()}?.data?.lastOrNull()?.let { data ->
            localDataSource.getRemoteKeysById(data.product.id.toString())
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}