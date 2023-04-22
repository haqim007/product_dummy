package dev.haqim.productdummy.feature.detail

import app.cash.turbine.test
import dev.haqim.productdummy.core.data.mechanism.Resource
import dev.haqim.productdummy.core.domain.usecase.ProductUseCase
import dev.haqim.productdummy.feature.util.DataDummy
import dev.haqim.productdummy.feature.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class DetailProductViewModelTest{

    @get:Rule
    val coroutineRule = MainCoroutineRule()
    
    private lateinit var viewModel: DetailProductViewModel
    @Mock
    private lateinit var useCase: ProductUseCase
    
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When processAction() GetProduct Should Update product State To Not Null`() = runTest{
        
        val product = DataDummy.productWithFavoriteEntity()
        
        `when`(useCase.getProduct(product)).thenReturn(flowOf(product))
        
        viewModel = DetailProductViewModel(useCase)
        
        viewModel.processAction(DetailProductUiAction.GetProduct(product))
        
        viewModel.uiState.test { 
            val state = awaitItem()
            
            assertNotNull(state.product)
            assertEquals(product, state.product)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When processAction() GetProduct Should Trigger useCase getProduct()`() = runTest{

        val product = DataDummy.productWithFavoriteEntity()

        `when`(useCase.getProduct(product)).thenReturn(flowOf(product))

        viewModel = DetailProductViewModel(useCase)

        viewModel.processAction(DetailProductUiAction.GetProduct(product))

        verify(useCase).getProduct(product)
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When processAction() ToggleFavorite Should Update addToFavoriteResult state to Resource_Success`() = runTest{

        val product = DataDummy.productWithFavoriteEntity()

        `when`(useCase.toggleFavorite(product)).thenReturn(flowOf(Resource.Success(true)))

        viewModel = DetailProductViewModel(useCase)

        viewModel.processAction(DetailProductUiAction.ToggleFavorite(product))

        viewModel.uiState.test { 
            
            val state = awaitItem()
            
            assertTrue(state.addToFavoriteResult.data ?: false)
            
            cancelAndIgnoreRemainingEvents()
            
        }
        
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When processAction() ToggleFavorite Should Update addToFavoriteResult state to Resource_Error`() = runTest{

        val product = DataDummy.productWithFavoriteEntity()

        `when`(useCase.toggleFavorite(product)).thenReturn(flowOf(Resource.Error("error", false)))

        viewModel = DetailProductViewModel(useCase)

        viewModel.processAction(DetailProductUiAction.ToggleFavorite(product))

        viewModel.uiState.test {

            val state = awaitItem()

            assertFalse(state.addToFavoriteResult.data ?: true)
            assertEquals("error", state.addToFavoriteResult.message)

            cancelAndIgnoreRemainingEvents()

        }

    }
    
}