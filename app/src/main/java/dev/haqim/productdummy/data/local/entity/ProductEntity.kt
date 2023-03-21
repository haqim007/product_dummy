package dev.haqim.productdummy.data.local.entity

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val TABLE_PRODUCTS = "products"
@Entity(tableName = TABLE_PRODUCTS)
data class ProductEntity(
	@ColumnInfo(name = "discount_percentage")
	val discountPercentage: Double,
	val thumbnail: String,
	val images: String,
	val price: Int,
	val rating: Double,
	val description: String,
	@PrimaryKey val id: Int,
	val title: String,
	val stock: Int,
	val category: String,
	val brand: String
)