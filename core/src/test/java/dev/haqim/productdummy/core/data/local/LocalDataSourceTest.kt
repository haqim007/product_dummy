package dev.haqim.productdummy.core.data.local

import androidx.paging.PagingSource
import app.cash.turbine.test
import dev.haqim.productdummy.core.data.local.entity.ProductFavoriteEntity
import dev.haqim.productdummy.core.data.local.room.ProductDatabase
import dev.haqim.productdummy.core.util.DataDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import kotlin.random.Random
import kotlin.random.nextInt

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class LocalDataSourceTest {
    
    @Mock
    private lateinit var database: ProductDatabase
    private val remoteKeysDao = FakeRemoteKeysDao()
    private val productDao = FakeProductDao()
    
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setUp() {
        `when`(database.remoteKeysDao()).thenReturn(remoteKeysDao)
        `when`(database.productDao()).thenReturn(productDao)

        localDataSource = LocalDataSource(database)
    }

    
    @Test
    fun `insert remotekeys`() = runTest{
        
        localDataSource.insertRemoteKeys(
            DataDummy.remoteKeys()
        )
        
        val randomIndex = Random.nextInt(1..4)
        assertEquals(
            DataDummy.remoteKeys().find { it.id == randomIndex.toString() }, 
            localDataSource.getRemoteKeysById(randomIndex.toString())
        )
        
    }

    @Test
    fun `remove remotekeys`() = runTest{

        localDataSource.insertRemoteKeys(
            DataDummy.remoteKeys()
        )

        val randomIndex = Random.nextInt(1..4)
        assertEquals(
            DataDummy.remoteKeys().find { it.id == randomIndex.toString() },
            localDataSource.getRemoteKeysById(randomIndex.toString())
        )
        
        localDataSource.clearRemoteKeys()
        assertNull(localDataSource.getRemoteKeysById(randomIndex.toString()))

    }

    @Test
    fun `insert products`() = runTest{

        localDataSource.insertAllProducts(
            DataDummy.productsEntity()
        )

        val randomIndex = Random.nextInt(1..4)
        localDataSource.getProductById(randomIndex).test {
            val product = awaitItem()
            assertEquals(
                DataDummy.productsEntity().find { it.id == randomIndex }?.id,
                product?.product?.id
            )
            
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `remove products`() = runTest{

        localDataSource.insertAllProducts(
            DataDummy.productsEntity()
        )

        val randomIndex = Random.nextInt(1..4)
        localDataSource.getProductById(randomIndex).test {
            val product = awaitItem()
            assertEquals(
                DataDummy.productsEntity().find { it.id == randomIndex }?.id,
                product?.product?.id
            )

            cancelAndIgnoreRemainingEvents()
        }
        
        localDataSource.clearAllProducts()
        localDataSource.getProductById(randomIndex).test {
            val product = awaitItem()
            assertNull(
                product
            )

            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `get product by id when does not exist`() = runTest{

        localDataSource.insertAllProducts(
            DataDummy.productsEntity()
        )

        localDataSource.getProductById(100).test {
            val product = awaitItem()
            assertNull(
                product
            )

            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `add product to favorite`() = runTest{

        // insert products
        localDataSource.insertAllProducts(
            DataDummy.productsEntity()
        )

        // get random id based on data in [DataDummy]
        val randomIndex = Random.nextInt(1..4)
        localDataSource.getProductById(randomIndex).test {
            
            // get data product
            val product = awaitItem()
            
            // check inserted product (random 1)
            assertEquals(
                DataDummy.productsEntity().find { it.id == randomIndex }?.id,
                product?.product?.id
            )

            // isFavorite should be false
            assertFalse(
                product?.isFavorite ?: true
            )

            val productId = product?.product?.id ?: 0

            // insert product to favorite
            localDataSource.insertFavoriteProduct(ProductFavoriteEntity(1, productId))
            
            // get updated product
            val favoriteProduct = awaitItem()

            // retest data product
            assertEquals(
                product?.product?.id,
                favoriteProduct?.product?.id
            )
            
            // isFavorite should be true
            assertEquals(
                true,
                favoriteProduct?.isFavorite
            )
            
            cancelAndIgnoreRemainingEvents()
            
        }

    }

    @Test
    fun `remove product to favorite`() = runTest{

        // insert products
        localDataSource.insertAllProducts(
            DataDummy.productsEntity()
        )

        // get random id based on data in [DataDummy]
        val randomIndex = Random.nextInt(1..4)
        localDataSource.getProductById(randomIndex).test {

            // get data product
            val product = awaitItem()

            // check inserted product (random 1)
            assertEquals(
                DataDummy.productsEntity().find { it.id == randomIndex }?.id,
                product?.product?.id
            )

            // isFavorite should be false
            assertFalse(
                product?.isFavorite ?: true
            )

            val productId = product?.product?.id ?: 0

            // insert product to favorite
            localDataSource.insertFavoriteProduct(ProductFavoriteEntity(1, productId))

            // get updated product
            val favoriteProduct = awaitItem()

            // retest data product
            assertEquals(
                product?.product?.id,
                favoriteProduct?.product?.id
            )

            // isFavorite should be true
            assertEquals(
                true,
                favoriteProduct?.isFavorite
            )

            // remove product from favorite
            localDataSource.removeFavoriteProduct(productId)

            // check removed data product
            val removedFromFavorite = awaitItem()

            // isFavorite should be false
            assertEquals(
                false,
                removedFromFavorite?.isFavorite
            )

            cancelAndIgnoreRemainingEvents()

        }

    }

    @Test
    fun `get favorite products`() = runTest{

        // insert products
        localDataSource.insertAllProducts(
            DataDummy.productsEntity()
        )

        // get random id based on data in [DataDummy]
        val randomIndex = Random.nextInt(1..4)

        localDataSource.insertFavoriteProduct(ProductFavoriteEntity(1, randomIndex))

        //Assert
        val actual = localDataSource.getAllFavoriteProducts().load(
            PagingSource.LoadParams.Refresh(
                key = null, loadSize = 4, placeholdersEnabled = false
            )
        )
        
        assertEquals(randomIndex, (actual as PagingSource.LoadResult.Page).data[0].product.id)

    }
}