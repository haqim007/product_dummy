package dev.haqim.productdummy.domain.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Product(
    val discountPercentage: Double,
    val thumbnail: String,
    val images: List<String>,
    val price: Int,
    val rating: Int,
    val description: String,
    val id: Int,
    val title: String,
    val stock: Int,
    val category: String,
    val brand: String,
    val isFavorite: Boolean = false,
    val formattedPrice: String
) : Parcelable