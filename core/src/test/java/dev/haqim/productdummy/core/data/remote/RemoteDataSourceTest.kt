package dev.haqim.productdummy.core.data.remote


import dev.haqim.productdummy.core.data.remote.network.ApiService
import dev.haqim.productdummy.core.util.DataDummy
import dev.haqim.productdummy.core.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.HttpException
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class RemoteDataSourceTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var service: ApiService

    private lateinit var remoteDataSource: RemoteDataSource
    
    @Test
    fun `When getProducts() then return success`() = runTest{

        val skip = 0
        val limit = 30
        val dummyProducts = DataDummy.productsResponse()
        
        Mockito.`when`(service.getProducts(limit, skip)).thenReturn(
            dummyProducts
        )
        remoteDataSource = RemoteDataSource(service)
        val products = remoteDataSource.getProducts(limit, skip)
        
        Mockito.verify(service).getProducts(limit, skip)

        assertEquals(products, Result.success(dummyProducts))
        assertEquals(products.getOrNull()?.products, dummyProducts.products)

    }

    @Test
    fun `When getProducts() then return failure`() = runTest{

        val skip = 0
        val limit = 30
        val exception = HttpException(
            Response.error<ResponseBody>(500, ("{\n" +
        "    \"error\": true,\n" +
        "    \"message\": \"Error\"\n" +
        "}").toResponseBody("plain/text".toMediaTypeOrNull())
        ))

        Mockito.`when`(service.getProducts(limit, skip)).thenThrow(
            exception
        )
        remoteDataSource = RemoteDataSource(service)
        val products = remoteDataSource.getProducts(limit, skip)

        Mockito.verify(service).getProducts(limit, skip)

        assertTrue(products.isFailure)
        assertEquals(products.getOrNull()?.products, null)

    }
}