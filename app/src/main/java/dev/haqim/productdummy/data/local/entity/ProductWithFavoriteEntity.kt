package dev.haqim.productdummy.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation
import androidx.room.Transaction

data class ProductWithFavoriteEntity(
	@Embedded
	val product: ProductEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "product_id"
	)
	val favorite: ProductFavoriteEntity?
){
	val isFavorite: Boolean
		get() = favorite != null
}