package dev.haqim.productdummy.core.data.local.entity

import androidx.room.*

const val TABLE_PRODUCTS_FAVORITE = "products_favorite"
@Entity(tableName = dev.haqim.productdummy.core.data.local.entity.TABLE_PRODUCTS_FAVORITE, foreignKeys = [
	ForeignKey(
		entity = dev.haqim.productdummy.core.data.local.entity.ProductEntity::class,
		parentColumns = ["id"],
		childColumns = ["product_id"],
		onDelete = ForeignKey.NO_ACTION
		)
	],
	indices = [Index(value = ["product_id"])],
)
data class ProductFavoriteEntity(
	@PrimaryKey(autoGenerate = true) val id: Int = 0,
	@ColumnInfo(name = "product_id")
	val productId: Int
)