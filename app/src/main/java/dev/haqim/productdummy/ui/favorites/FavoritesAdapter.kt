package dev.haqim.productdummy.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import dev.haqim.productdummy.R
import dev.haqim.productdummy.databinding.ItemProductBinding
import dev.haqim.productdummy.domain.model.Product
import java.text.NumberFormat
import java.util.*


class FavoritesAdapter(private val listener: FavoritesAdapterListener):
    PagingDataAdapter<Product, RecyclerView.ViewHolder>(DIFF_CALLBACK){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.onCreate(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val product = getItem(position)
        (holder as ViewHolder).onBind(product, listener)
    }

    class ViewHolder private constructor(private val binding: ItemProductBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(product: Product?, listener: FavoritesAdapterListener) {

            product?.let {
                Glide.with(itemView.context).load(it.images[0])
                    .placeholder(R.drawable.baseline_downloading_24)
                    .transform(CenterInside(), RoundedCorners(24))
                    .into(binding.productImage)
                binding.productName.text = it.title
                binding.productPrice.text = it.formattedPrice
                binding.root.setOnClickListener {
                    listener.onClickProduct(product)
                }
                binding.productRating.progress = it.rating
                val favoriteDrawable = if(it.isFavorite){
                    AppCompatResources.getDrawable(binding.root.context, R.drawable.ic_active_favorite_24)
                }else{
                    AppCompatResources.getDrawable(binding.root.context, R.drawable.ic_inactive_favorite_24)
                }
                binding.btnFavorite.setImageDrawable(favoriteDrawable)
                binding.btnFavorite.setOnClickListener { 
                    listener.onRemoveFavorite(product)
                }
            }
        }

        companion object{
            fun onCreate(parent: ViewGroup): ViewHolder {
                val itemView =
                    ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(itemView)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {

            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }
    }
}

interface FavoritesAdapterListener{
    fun onClickProduct(product: Product)
    fun onRemoveFavorite(product: Product)
}