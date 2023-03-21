package dev.haqim.productdummy.feature.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import dev.haqim.productdummy.R
import dev.haqim.productdummy.databinding.FragmentProductListBinding
import dev.haqim.productdummy.core.domain.model.Product
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModels()

    private lateinit var uiAction: (FavoritesUiAction) -> Boolean
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        
        requireActivity().setTitle(R.string.favorite_products_title)
        
        val uiState = viewModel.uiState
        uiAction = {action: FavoritesUiAction -> viewModel.processAction(action)}
        
        bindState(uiState, uiAction)

        //bind product list
        bindFavorites(uiAction)

        return binding.root
    }

    private fun bindFavorites(uiAction: (FavoritesUiAction) -> Boolean) {
        val adapter = FavoritesAdapter(object : FavoritesAdapterListener {
            override fun onClickProduct( product: Product) {
                uiAction(FavoritesUiAction.OpenProduct(product))
            }

            override fun onRemoveFavorite(product: Product) {
                uiAction(FavoritesUiAction.RemoveFavorite(product))
            }
        })
        binding.rvProducts.adapter = adapter
        binding.rvProducts.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pagingDataFlow.collect(adapter::submitData)
        }
    }

    private fun bindState(
        uiState: StateFlow<FavoritesUiState>,
        uiAction: (FavoritesUiAction) -> Boolean
    ) {
        
        //bind open product
        val openProductFlow = uiState.map { it.productToOpen }.distinctUntilChanged()
        viewLifecycleOwner.lifecycleScope.launch { 
            openProductFlow.collect{product ->
                product?.let{
                    val action =
                        FavoritesFragmentDirections
                            .actionFavoriteProductFragmentToDetailProductFragment(it)
                    findNavController().navigate(action)
                    uiAction(FavoritesUiAction.AfterOpenProduct)
                }
            }
        }
        
    }

}