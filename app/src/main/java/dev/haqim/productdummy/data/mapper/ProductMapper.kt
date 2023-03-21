package dev.haqim.productdummy.data.mapper

import dev.haqim.productdummy.data.local.entity.ProductFavoriteEntity
import dev.haqim.productdummy.domain.model.Product

fun Product.toProductFavoriteEntity() = 
    ProductFavoriteEntity(productId = this.id)