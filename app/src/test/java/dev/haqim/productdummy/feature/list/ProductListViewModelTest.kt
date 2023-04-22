package dev.haqim.productdummy.feature.list

import androidx.paging.AsyncPagingDataDiffer
import app.cash.turbine.test
import dev.haqim.productdummy.core.data.mechanism.Resource
import dev.haqim.productdummy.core.domain.usecase.ProductUseCase
import dev.haqim.productdummy.core.ui.ProductsAdapter
import dev.haqim.productdummy.core.util.noopListUpdateCallback
import dev.haqim.productdummy.feature.util.DataDummy
import dev.haqim.productdummy.feature.util.MainCoroutineRule
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
class ProductListViewModelTest{
    

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ProductListViewModel
    @Mock
    private lateinit var useCase: ProductUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When init Should call getProducts() from ProductUseCase Expect return empty`() =
        runTest{

            val expected = DataDummy.pagingDataProductsFlow(true)
            `when`(useCase.getProducts()).thenReturn(expected)

            viewModel = ProductListViewModel(useCase)

            val differ = AsyncPagingDataDiffer(
                diffCallback = ProductsAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                workerDispatcher = Dispatchers.Main,
            )

            viewModel.pagingDataFlow.test {

                differ.submitData(awaitItem())

                assertNotNull(differ.snapshot())
                assertEquals(0, differ.snapshot().size)
                cancelAndIgnoreRemainingEvents()
                verify(useCase).getProducts()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When init Should call getProducts() from ProductUseCase Expect return not empty`() =
        runTest{

            val expected = DataDummy.pagingDataProductsFlow()
            `when`(useCase.getProducts()).thenReturn(expected)

            viewModel = ProductListViewModel(useCase)

            val differ = AsyncPagingDataDiffer(
                diffCallback = ProductsAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                workerDispatcher = Dispatchers.Main,
            )

            viewModel.pagingDataFlow.test {

                differ.submitData(awaitItem())

                assertNotNull(differ.snapshot())
                assertTrue(differ.snapshot().size > 9)
                cancelAndIgnoreRemainingEvents()
                verify(useCase).getProducts()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When processAction() ToggleFavorite Should Trigger useCase_toggleFavorite`() = runTest{
        val product = DataDummy.productWithFavoriteEntity()
        `when`(useCase.toggleFavorite(product)).thenReturn(
            flowOf(Resource.Success(true))
        )

        viewModel = ProductListViewModel(useCase)

        viewModel.processAction(ProductListUiAction.ToggleFavorite(product))


        verify(useCase).toggleFavorite(product)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When processAction() OpenProduct Expect productToOpen state to be updated`() = runTest{
        viewModel = ProductListViewModel(useCase)
        val product = DataDummy.productWithFavoriteEntity()

        viewModel.processAction(ProductListUiAction.OpenProduct(product))

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(product, state.productToOpen)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When processAction() AfterOpenProduct Expect productToOpen state to be updated`() = runTest{
        viewModel = ProductListViewModel(useCase)
        val product = DataDummy.productWithFavoriteEntity()

        viewModel.processAction(ProductListUiAction.OpenProduct(product))

        viewModel.uiState.test {
            // on OpenProduct
            assertEquals(product, awaitItem().productToOpen)

            cancelAndIgnoreRemainingEvents()
        }


        viewModel.processAction(ProductListUiAction.AfterOpenProduct)

        viewModel.uiState.test {

            // on AfterOpenProduct
            assertNull(awaitItem().productToOpen)


            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When processAction() NavigateToFavorite Should Update navigateToFavorite State To True`() = runTest {
        viewModel = ProductListViewModel(useCase)

        viewModel.processAction(ProductListUiAction.NavigateToFavorite)

        viewModel.uiState.test {

            assertTrue(awaitItem().navigateToFavorite)


            cancelAndIgnoreRemainingEvents()
        }
        
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When processAction() NavigateToFavorite Should Update navigateToFavorite State To False On The Second Time`() = runTest {
        viewModel = ProductListViewModel(useCase)

        viewModel.processAction(ProductListUiAction.NavigateToFavorite)

        viewModel.uiState.test {

            assertTrue(awaitItem().navigateToFavorite)


            cancelAndIgnoreRemainingEvents()
        }

        viewModel.processAction(ProductListUiAction.NavigateToFavorite)

        viewModel.uiState.test {

            assertFalse(awaitItem().navigateToFavorite)
            
            cancelAndIgnoreRemainingEvents()
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When processAction() NavigateToAbout Should Update navigateToAbout State To True`() = runTest {
        viewModel = ProductListViewModel(useCase)

        viewModel.processAction(ProductListUiAction.NavigateToAbout)

        viewModel.uiState.test {

            assertTrue(awaitItem().navigateToAbout)


            cancelAndIgnoreRemainingEvents()
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When processAction() NavigateToAbout Should Update navigateToAbout State To False On The Second Time`() = runTest {
        viewModel = ProductListViewModel(useCase)

        viewModel.processAction(ProductListUiAction.NavigateToAbout)

        viewModel.uiState.test {

            assertTrue(awaitItem().navigateToAbout)


            cancelAndIgnoreRemainingEvents()
        }

        viewModel.processAction(ProductListUiAction.NavigateToAbout)

        viewModel.uiState.test {

            assertFalse(awaitItem().navigateToAbout)

            cancelAndIgnoreRemainingEvents()
        }

    }
}