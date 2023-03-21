package dev.haqim.productdummy.feature.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import dagger.hilt.android.AndroidEntryPoint
import dev.haqim.productdummy.R
import dev.haqim.productdummy.databinding.FragmentDetailProductBinding
import dev.haqim.productdummy.core.domain.model.Product
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailProductFragment : Fragment() {

    private var _binding: FragmentDetailProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailProductViewModel by viewModels()
    private lateinit var product: Product

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View{
        _binding = FragmentDetailProductBinding.inflate(inflater, container, false)

        val args: DetailProductFragmentArgs by navArgs()
        product = args.product

        requireActivity().title = product.title
        
        val uiState = viewModel.uiState
        val uiAction = { action: DetailProductUiAction -> viewModel.processAction(action) }
        
        // bind product data
        bindProductState(uiAction, uiState)

        binding.btnFavorite.setOnClickListener {
            uiAction(DetailProductUiAction.ToggleFavorite(product))
        }
        
        return binding.root
    }

    private fun bindProductState(
        uiAction: (DetailProductUiAction) -> Boolean,
        uiState: StateFlow<DetailProductUiState>,
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                uiAction(DetailProductUiAction.GetProduct(product))
            }
        }

        val productFlow = uiState.map { it.product }.distinctUntilChanged()
        viewLifecycleOwner.lifecycleScope.launch {
            productFlow.collect { productState ->
                productState?.let {
                    
                    product = it
                    
                    // set image list
                    val imageList = ArrayList<SlideModel>()
                    product.images.forEach {
                        imageList.add(SlideModel(imageUrl = it))
                    }
                    binding.productImages.setImageList(imageList, scaleType = ScaleTypes.FIT)

                    // set product name
                    binding.productName.text = it.title

                    // set product price
                    binding.productPrice.text = it.price.toString()

                    // set product rating
                    binding.productRating.progress = it.rating

                    // set product description
                    binding.productDescription.text = it.description

                    // set product favorite flag
                    val favoriteDrawable = if (it.isFavorite) {
                        AppCompatResources.getDrawable(
                            binding.root.context,
                            R.drawable.ic_active_favorite_24
                        )
                    } else {
                        AppCompatResources.getDrawable(
                            binding.root.context,
                            R.drawable.ic_inactive_favorite_24
                        )
                    }
                    binding.btnFavorite.setImageDrawable(favoriteDrawable)
                }
            }
        }
    }
    
    

}