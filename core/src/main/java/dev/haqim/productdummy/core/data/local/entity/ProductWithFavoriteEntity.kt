package dev.haqim.productdummy.core.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation
import androidx.room.Transaction

data class ProductWithFavoriteEntity(
    @Embedded
	val product: dev.haqim.productdummy.core.data.local.entity.ProductEntity,
    @Relation(
		parentColumn = "id",
		entityColumn = "product_id"
	)
	val favorite: dev.haqim.productdummy.core.data.local.entity.ProductFavoriteEntity?
){
	val isFavorite: Boolean
		get() = favorite != null
}