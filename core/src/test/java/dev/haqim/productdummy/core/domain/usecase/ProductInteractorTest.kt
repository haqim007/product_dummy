package dev.haqim.productdummy.core.domain.usecase

import androidx.paging.AsyncPagingDataDiffer
import app.cash.turbine.test
import dev.haqim.productdummy.core.data.mapper.toEntity
import dev.haqim.productdummy.core.data.mapper.toModel
import dev.haqim.productdummy.core.data.mechanism.Resource
import dev.haqim.productdummy.core.data.repository.ProductRepository
import dev.haqim.productdummy.core.ui.ProductsAdapter
import dev.haqim.productdummy.core.util.DataDummy
import dev.haqim.productdummy.core.util.MainCoroutineRule
import dev.haqim.productdummy.core.util.noopListUpdateCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ProductInteractorTest{

    @get:Rule
    val coroutineRule = MainCoroutineRule()
    
    private lateinit var productInteractor: ProductUseCase
    @Mock private lateinit var productRepository: ProductRepository
    
    @Test
    fun `When getProducts() Should return PagingData_Product`() = runTest{

        `when`(productRepository.getProducts()).thenReturn(
            DataDummy.pagingDataProductsFlow()
        )
        productInteractor = ProductInteractor(productRepository)

        val expected = DataDummy.productsWithFavoriteEntity()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ProductsAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        productInteractor.getProducts().test {
            verify(productRepository).getProducts()
            val pagingData = awaitItem()
            differ.submitData(pagingData)

            assertNotNull(differ.snapshot())
            assertEquals(expected.size, differ.snapshot().size)
            assertEquals(expected[0].toModel(), differ.snapshot()[0])

            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `When getFavoriteProducts() Should return PagingData_Product`() = runTest{

        `when`(productRepository.getFavoriteProducts()).thenReturn(
            DataDummy.pagingDataProductsFlow()
        )
        productInteractor = ProductInteractor(productRepository)

        val expected = DataDummy.productsWithFavoriteEntity()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ProductsAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        productInteractor.getFavoriteProducts().test {
            verify(productRepository).getFavoriteProducts()
            val pagingData = awaitItem()
            differ.submitData(pagingData)

            assertNotNull(differ.snapshot())
            assertEquals(expected.size, differ.snapshot().size)
            assertEquals(expected[0].toModel(), differ.snapshot()[0])

            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `When toggleFavorite() Should Return True`() = runTest {
        val product = DataDummy.productsWithFavoriteEntity()[0].toModel()  
        val expected = Resource.Success(true)
        `when`(productRepository.toggleFavorite(product)).thenReturn(
            flowOf(expected)
        )
        
        productInteractor = ProductInteractor(productRepository)
        
        productInteractor.toggleFavorite(product).test { 
            val actual = awaitItem()
            
            assertTrue(actual.data ?: false)
            verify(productRepository).toggleFavorite(product)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When toggleFavorite() Should Return False`() = runTest {
        val product = DataDummy.productsWithFavoriteEntity()[0].toModel()
        val expected = Resource.Error("Error",false)
        `when`(productRepository.toggleFavorite(product)).thenReturn(
            flowOf(expected)
        )

        productInteractor = ProductInteractor(productRepository)

        productInteractor.toggleFavorite(product).test {
            val actual = awaitItem()

            assertFalse(actual.data ?: false)
            assertEquals(actual.message, expected.message)
            verify(productRepository).toggleFavorite(product)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When removeFavorite() Should Return True`() = runTest {
        val product = DataDummy.productsWithFavoriteEntity()[0].toModel()
        val expected = Resource.Success(true)
        `when`(productRepository.removeFavorite(product)).thenReturn(
            flowOf(expected)
        )

        productInteractor = ProductInteractor(productRepository)

        productInteractor.removeFavorite(product).test {
            val actual = awaitItem()

            assertTrue(actual.data ?: false)
            verify(productRepository).removeFavorite(product)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When removeFavorite() Should Return False`() = runTest {
        val product = DataDummy.productsWithFavoriteEntity()[0].toModel()
        val expected = Resource.Error("Error",false)
        `when`(productRepository.removeFavorite(product)).thenReturn(
            flowOf(expected)
        )

        productInteractor = ProductInteractor(productRepository)

        productInteractor.removeFavorite(product).test {
            val actual = awaitItem()

            assertFalse(actual.data ?: false)
            assertEquals(actual.message, expected.message)
            verify(productRepository).removeFavorite(product)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When getProduct() Should Return Not Null`() = runTest {
        val product = DataDummy.productsWithFavoriteEntity()[0].toModel()
        `when`(productRepository.getProduct(product)).thenReturn(
            flowOf(product)
        )

        productInteractor = ProductInteractor(productRepository)

        productInteractor.getProduct(product).test {
            val actual = awaitItem()

            assertNotNull(actual)
            verify(productRepository).getProduct(product)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When getProduct() Should Return Null`() = runTest {
        val product = DataDummy.productsWithFavoriteEntity()[0].toModel()
        `when`(productRepository.getProduct(product)).thenReturn(
            flowOf(null)
        )

        productInteractor = ProductInteractor(productRepository)

        productInteractor.getProduct(product).test {
            val actual = awaitItem()

            assertNull(actual)
            verify(productRepository).getProduct(product)

            cancelAndIgnoreRemainingEvents()
        }
    }
    
}