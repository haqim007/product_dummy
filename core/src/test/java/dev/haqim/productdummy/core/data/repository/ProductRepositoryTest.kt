package dev.haqim.productdummy.core.data.repository

import app.cash.turbine.test
import dev.haqim.productdummy.core.data.local.LocalDataSource
import dev.haqim.productdummy.core.data.mapper.toProductFavoriteEntity
import dev.haqim.productdummy.core.data.mechanism.Resource
import dev.haqim.productdummy.core.data.remotemediator.ProductRemoteMediator
import dev.haqim.productdummy.core.util.DataDummy
import dev.haqim.productdummy.core.util.MemoryPagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ProductRepositoryTest{
    
    private lateinit var repository: ProductRepository
    @Mock 
    private lateinit var localDataSource: LocalDataSource
    @Mock
    private lateinit var remoteMediator: ProductRemoteMediator
    
    
    @Test
    fun `When getProducts Should Return Not Null`() = runTest { 
        val products = DataDummy.productsResponse().products.toProductFavoriteEntity()
        `when`(localDataSource.getAllProducts()).thenReturn(
            MemoryPagingSource(products)
        )
       
        repository = ProductRepository(remoteMediator, localDataSource)
        repository.getProducts().test {
            assertNotNull(awaitItem())
            
            cancelAndIgnoreRemainingEvents()
        }
        
    }

    @Test
    fun `When getFavoriteProducts Should Return Not Null`() = runTest {
        val products = DataDummy.productResponseToFav()

        `when`(localDataSource.getAllFavoriteProducts()).thenReturn(
            MemoryPagingSource(products)
        )
        repository = ProductRepository(remoteMediator, localDataSource)
        val actual = repository.getFavoriteProducts().first()
        assertNotNull(actual)
    }

    @Test
    fun `When toggleFavorite To Add Product As Favorite Then Return Success`() = runTest {
        val product = DataDummy.productWithFavoriteEntity()
        repository = ProductRepository(remoteMediator, localDataSource)
        
        repository.toggleFavorite(product).test { 
            assertTrue(awaitItem() is Resource.Loading)
            
            assertTrue(awaitItem() is Resource.Success)
            
            cancelAndIgnoreRemainingEvents()
            
        }
        
    }

    @Test
    fun `When toggleFavorite To Add Product As Favorite Then Return Error`() = runTest {
        val product = DataDummy.productWithFavoriteEntity()
        
        `when`(localDataSource.insertFavoriteProduct(product.toProductFavoriteEntity())).thenThrow(
            RuntimeException("Error nih")
        )
        repository = ProductRepository(remoteMediator, localDataSource)

        repository.toggleFavorite(product).test {
            assertTrue(awaitItem() is Resource.Loading)

            val emissionError = awaitItem()
            assertTrue(emissionError is Resource.Error)
            
            assertEquals("Error nih", emissionError.message)

            cancelAndIgnoreRemainingEvents()

        }

    }

    @Test
    fun `When toggleFavorite To Remove Product As Favorite Then Return Success`() = runTest {
        val product = DataDummy.productWithFavoriteEntity(true)
        repository = ProductRepository(remoteMediator, localDataSource)

        repository.toggleFavorite(product).test {
            assertTrue(awaitItem() is Resource.Loading)

            assertTrue(awaitItem() is Resource.Success)

            cancelAndIgnoreRemainingEvents()

        }

    }

    @Test
    fun `When toggleFavorite To Remove Product As Favorite Then Return Error`() = runTest {
        val product = DataDummy.productWithFavoriteEntity(true)

        `when`(localDataSource.removeFavoriteProduct(product.id)).thenThrow(
            RuntimeException("Error nih")
        )
        repository = ProductRepository(remoteMediator, localDataSource)

        repository.toggleFavorite(product).test {
            assertTrue(awaitItem() is Resource.Loading)

            val emissionError = awaitItem()
            assertTrue(emissionError is Resource.Error)

            assertEquals("Error nih", emissionError.message)

            cancelAndIgnoreRemainingEvents()

        }

    }
    
    
}