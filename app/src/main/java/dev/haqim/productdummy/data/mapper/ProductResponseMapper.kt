package dev.haqim.productdummy.data.mapper

import dev.haqim.productdummy.data.local.entity.ProductEntity
import dev.haqim.productdummy.data.remote.response.ProductsItemResponse
import dev.haqim.productdummy.data.remote.response.ProductsResponse
import dev.haqim.productdummy.domain.model.Product
import java.text.NumberFormat
import java.util.*

fun ProductsResponse.toModel(){
    this.products.map { 
        it.toModel()
    }
}

fun ProductsItemResponse.toModel() = 
    Product(
        discountPercentage,
        thumbnail,
        images,
        price,
        rating = (this.rating / 5 * 100).toInt(),
        description,
        id,
        title,
        stock,
        category,
        brand,
        formattedPrice = NumberFormat.getCurrencyInstance(Locale.getDefault()).format(this.price),
    )

fun  List<ProductsItemResponse>?.toEntity(): List<ProductEntity> {
    return this?.map {
        it.toEntity()
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