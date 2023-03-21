package dev.haqim.productdummy.core.data.mapper

import dev.haqim.productdummy.core.data.local.entity.ProductWithFavoriteEntity
import dev.haqim.productdummy.core.domain.model.Product
import java.text.NumberFormat
import java.util.*


fun dev.haqim.productdummy.core.data.local.entity.ProductWithFavoriteEntity.toModel() =
    Product(
        discountPercentage = this.product.discountPercentage,
        thumbnail = this.product.thumbnail,
        images = this.product.images.split(","),
        price = this.product.price,
        formattedPrice = NumberFormat.getCurrencyInstance(Locale.getDefault()).format(this.product.price),
        rating = (this.product.rating / 5 * 100).toInt(),
        description = this.product.description,
        id = this.product.id,
        title = this.product.title,
        stock = this.product.stock,
        category = this.product.category,
        brand = this.product.brand,
        isFavorite = this.isFavorite,
    )