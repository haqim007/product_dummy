package dev.haqim.productdummy.core.data.mapper

import dev.haqim.productdummy.core.domain.model.Product

fun Product.toProductFavoriteEntity() =
    dev.haqim.productdummy.core.data.local.entity.ProductFavoriteEntity(productId = this.id)