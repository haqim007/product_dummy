package dev.haqim.productdummy.core.data.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dev.haqim.productdummy.core.data.local.LocalDataSource
import dev.haqim.productdummy.core.data.local.entity.ProductWithFavoriteEntity
import dev.haqim.productdummy.core.data.remote.RemoteDataSource
import dev.haqim.productdummy.core.data.remote.response.ProductsResponse
import dev.haqim.productdummy.core.util.DataDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ProductRemoteMediatorTest{
    
    private lateinit var productRemoteMediator: ProductRemoteMediator
    
    @Mock
    private lateinit var localDataSource: LocalDataSource
    @Mock
    private lateinit var remoteDataSource: RemoteDataSource
    
    @Before
    fun setup(){
        productRemoteMediator = ProductRemoteMediator(localDataSource, remoteDataSource)
    }
    
    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `When Load() with response data not empty Should return Success And EndOfPagination false`() = runTest {
        val pagingState = PagingState<Int, ProductWithFavoriteEntity>(
            listOf(),
            null,
            PagingConfig(pageSize = 4),
            0
        )

        val response = Result.success(DataDummy.productsResponse())
        `when`(remoteDataSource.getProducts(4, 0)).thenReturn(response)

        val result = productRemoteMediator.load(LoadType.REFRESH, pagingState)

        verify(remoteDataSource).getProducts(4, 0)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `When Load() with response data empty Should return Success And EndOfPagination true`() = runTest {
        val pagingState = PagingState<Int, ProductWithFavoriteEntity>(
            listOf(),
            null,
            PagingConfig(pageSize = 4),
            0
        )

        val response = Result.success(
            ProductsResponse(
                products = listOf(),
                total = 0,
                skip = 0,
                limit = 4
            )
        )
        
        `when`(remoteDataSource.getProducts(4, 0)).thenReturn(response)

        val result = productRemoteMediator.load(LoadType.REFRESH, pagingState)

        verify(remoteDataSource).getProducts(4, 0)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `When Load() Should return Error And EndOfPagination true`() = runTest {
        val pagingState = PagingState<Int, ProductWithFavoriteEntity>(
            listOf(),
            null,
            PagingConfig(pageSize = 4),
            0
        )

        val exception = RuntimeException("Something went wrong!")
        `when`(remoteDataSource.getProducts(4, 0)).thenReturn(Result.failure(exception))

        val productRemoteMediator = ProductRemoteMediator(localDataSource, remoteDataSource)


        val result = productRemoteMediator.load(LoadType.REFRESH, pagingState)

        verify(remoteDataSource).getProducts(4, 0)

        assertTrue(result is RemoteMediator.MediatorResult.Error)
        assertEquals("Something went wrong!", (result as RemoteMediator.MediatorResult.Error).throwable.localizedMessage)
    }
}