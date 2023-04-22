package dev.haqim.productdummy.feature.util

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.haqim.productdummy.core.data.local.entity.ProductEntity
import dev.haqim.productdummy.core.data.local.entity.ProductWithFavoriteEntity
import dev.haqim.productdummy.core.data.mapper.toEntity
import dev.haqim.productdummy.core.data.remote.response.ProductsItemResponse
import kotlinx.coroutines.flow.Flow


class ProductRemoteMediatorMock: PagingSource<Int, Flow<List<ProductWithFavoriteEntity>>>(){

    override fun getRefreshKey(state: PagingState<Int, Flow<List<ProductWithFavoriteEntity>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Flow<List<ProductWithFavoriteEntity>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

    companion object{
        fun snapshot(items:   List<ProductWithFavoriteEntity>): PagingData<ProductWithFavoriteEntity> {
            return PagingData.from(
                items
            )
        }
    }

}