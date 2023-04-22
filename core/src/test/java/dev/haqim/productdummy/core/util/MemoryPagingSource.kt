package dev.haqim.productdummy.core.util

import androidx.paging.PagingSource
import androidx.paging.PagingState


class MemoryPagingSource<T : Any>(
    private val data: List<T>,
    private val pageSize: Int = 10
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val pageNumber = params.key ?: 0
        val start = pageNumber * pageSize
        val end = start + pageSize

        return try {
            val items = data.subList(start.coerceAtMost(data.size), end.coerceAtMost(data.size))
            LoadResult.Page(
                data = items,
                prevKey = if (pageNumber > 0) pageNumber - 1 else null,
                nextKey = if (items.size == pageSize) pageNumber + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
