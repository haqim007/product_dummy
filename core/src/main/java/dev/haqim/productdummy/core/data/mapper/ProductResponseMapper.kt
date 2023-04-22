package dev.haqim.productdummy.core.data.mapper

import dev.haqim.productdummy.core.data.local.entity.ProductEntity
import dev.haqim.productdummy.core.data.local.entity.ProductWithFavoriteEntity
import dev.haqim.productdummy.core.data.remote.response.ProductsItemResponse

fun  List<ProductsItemResponse>?.toEntity(): List<ProductEntity> {
    return this?.map {
        it.toEntity()
    } ?: listOf()
}

fun  List<ProductsItemResponse>?.toProductFavoriteEntity(): List<ProductWithFavoriteEntity> {
    return this?.map {
        ProductWithFavoriteEntity(it.toEntity(), null)
    } ?: listOf()
}

fun ProductsItemResponse.toEntity() =
    ProductEntity(
        discountPercentage,
        thumbnail,
        images = images.joinToString(","),
        price,
        rating,
        description,
        id,
        title,
        stock,
        category,
        brand
    )