package dev.haqim.productdummy.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dev.haqim.productdummy.R
import dev.haqim.productdummy.core.data.mechanism.Resource
import dev.haqim.productdummy.core.domain.model.Product
import dev.haqim.productdummy.databinding.FragmentProductListBinding
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class FavoritesFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: FavoritesViewModel = FavoritesViewModel(get())
    
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        
        requireActivity().setTitle(R.string.favorite_products_title)
        
        val uiState = viewModel.uiState
        val uiAction = {action: FavoritesUiAction -> viewModel.processAction(action)}
        
        bindState(uiState, uiAction)

//        bind product list
        bindFavorites(uiState, uiAction)

        return binding.root
    }

    private fun bindFavorites(uiState: StateFlow<FavoritesUiState>, uiAction: (FavoritesUiAction) -> Boolean) {
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

        val addToFavoriteResult = uiState.map { it.addToFavoriteResult }.distinctUntilChanged()
        viewLifecycleOwner.lifecycleScope.launch {
            addToFavoriteResult.collect{
                when (it) {
                    is Resource.Success -> {
                        Toast.makeText(requireContext(), R.string.success_to_update_favorite, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), R.string.failed_to_update_favorite, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        Toast.makeText(requireContext(), R.string.please_wait, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
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
                    val bundle =  Bundle()
                    bundle.putParcelable("product", it)
                    findNavController().navigate(R.id.detailProductFragment, bundle)
                    uiAction(FavoritesUiAction.AfterOpenProduct)
                }
            }
        }
        
    }

}