package dev.haqim.productdummy.core.ui

import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.haqim.productdummy.core.domain.model.Product


abstract class ProductsAdapter:
    PagingDataAdapter<Product, RecyclerView.ViewHolder>(DIFF_CALLBACK){


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