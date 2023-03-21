package dev.haqim.productdummy.feature.list

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import dev.haqim.productdummy.R
import dev.haqim.productdummy.databinding.FragmentProductListBinding
import dev.haqim.productdummy.core.domain.model.Product
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductListViewModel by viewModels()
    private lateinit var uiAction: (ProductListUiAction) -> Boolean
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        requireActivity().setTitle(R.string.app_name)
        val uiState = viewModel.uiState
        uiAction = {action: ProductListUiAction -> viewModel.processAction(action)}
        
        bindState(uiState, uiAction)

        //bind product list
        bindProductList(uiAction)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.product_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.menu_to_favorite -> {
                        uiAction(ProductListUiAction.NavigateToFavorite)
                        true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        
    }
    
    

    private fun bindProductList(uiAction: (ProductListUiAction) -> Boolean) {
        val adapter = ProductListAdapter(object : ProductListAdapterListener {
            override fun onClickProduct( product: Product) {
                uiAction(ProductListUiAction.OpenProduct(product))
            }

            override fun onSetFavorite(product: Product) {
                uiAction(ProductListUiAction.ToggleFavorite(product))
            }
        })
        binding.rvProducts.adapter = adapter
        binding.rvProducts.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        lifecycleScope.launch {
            viewModel.pagingDataFlow.collect(adapter::submitData)
        }
    }

    private fun bindState(
        uiState: StateFlow<ProductListUiState>,
        uiAction: (ProductListUiAction) -> Boolean
    ) {
        
        //bind open product
        bindOpenProduct(uiState, uiAction)
        
        //bind open favorite products
        bindNavToFavoriteProducts(uiState, uiAction)

    }

    private fun bindNavToFavoriteProducts(
        uiState: StateFlow<ProductListUiState>,
        uiAction: (ProductListUiAction) -> Boolean,
    ) {
        val favoriteProductsFlow = uiState.map { it.navigateToFavorite }.distinctUntilChanged()
        viewLifecycleOwner.lifecycleScope.launch {
            favoriteProductsFlow.collect {
                if (it) {
                    findNavController().navigate(R.id.action_productListFragment_to_favoriteProductFragment)
                    uiAction(ProductListUiAction.NavigateToFavorite)
                }
            }
        }
    }

    private fun bindOpenProduct(
        uiState: StateFlow<ProductListUiState>,
        uiAction: (ProductListUiAction) -> Boolean,
    ) {
        val openProductFlow = uiState.map { it.productToOpen }.distinctUntilChanged()
        viewLifecycleOwner.lifecycleScope.launch {
            openProductFlow.collectLatest { product ->
                if(product != null){
                    val action =
                        ProductListFragmentDirections.actionProductListFragmentToDetailProductFragment(
                            product
                        )
                    findNavController()
                        .navigate(action)
                }
                uiAction(ProductListUiAction.AfterOpenProduct)
            }
        }
    }

}