package dev.haqim.productdummy.favorites

import androidx.paging.AsyncPagingDataDiffer
import app.cash.turbine.test
import dev.haqim.productdummy.core.data.mapper.toModel
import dev.haqim.productdummy.core.data.mechanism.Resource
import dev.haqim.productdummy.core.domain.usecase.ProductUseCase
import dev.haqim.productdummy.core.ui.ProductsAdapter
import dev.haqim.productdummy.core.util.noopListUpdateCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
class FavoritesViewModelTest{

    @get:Rule
    val coroutineRule = MainCoroutineRule()
    
    private lateinit var viewModel: FavoritesViewModel
    @Mock
    private lateinit var useCase: ProductUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When init Should call getProducts() from ProductUseCase Expect return empty`() =
        runTest{

            val expected = DataDummy.pagingDataProductsFlow(true)
            `when`(useCase.getFavoriteProducts()).thenReturn(expected)
            
            viewModel = FavoritesViewModel(useCase)

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
                verify(useCase).getFavoriteProducts()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When init Should call getProducts() from ProductUseCase Expect return not empty`() =
        runTest{

            val expected = DataDummy.pagingDataProductsFlow()
            `when`(useCase.getFavoriteProducts()).thenReturn(expected)

            viewModel = FavoritesViewModel(useCase)

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
                verify(useCase).getFavoriteProducts()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When processAction() RemoveFavorite Should Trigger useCase_removeFavorite`() = runTest{
        val product = DataDummy.productsWithFavoriteEntity()[0].toModel()
        `when`(useCase.removeFavorite(product)).thenReturn(
            flowOf(Resource.Success(true))
        )

        viewModel = FavoritesViewModel(useCase)

        viewModel.processAction(FavoritesUiAction.RemoveFavorite(product))


        verify(useCase).removeFavorite(product)
        
        
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When processAction() OpenProduct Expect productToOpen state to be updated`() = runTest{
        viewModel = FavoritesViewModel(useCase)
        val product = DataDummy.productsWithFavoriteEntity()[0].toModel()

        viewModel.processAction(FavoritesUiAction.OpenProduct(product))

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(product, state.productToOpen)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When processAction() AfterOpenProduct Expect productToOpen state to be updated`() = runTest{
        viewModel = FavoritesViewModel(useCase)
        val product = DataDummy.productsWithFavoriteEntity()[0].toModel()

        viewModel.processAction(FavoritesUiAction.OpenProduct(product))

        viewModel.uiState.test {
            // on OpenProduct
            assertEquals(product, awaitItem().productToOpen)
            
            cancelAndIgnoreRemainingEvents()
        }


        viewModel.processAction(FavoritesUiAction.AfterOpenProduct)

        viewModel.uiState.test {

            // on AfterOpenProduct
            assertNull(awaitItem().productToOpen)


            cancelAndIgnoreRemainingEvents()
        }
    }
}