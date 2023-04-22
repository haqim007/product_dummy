package dev.haqim.productdummy.core.util

import androidx.paging.PagingData
import androidx.paging.map
import com.google.gson.Gson
import dev.haqim.productdummy.core.data.local.entity.ProductEntity
import dev.haqim.productdummy.core.data.local.entity.ProductFavoriteEntity
import dev.haqim.productdummy.core.data.local.entity.ProductWithFavoriteEntity
import dev.haqim.productdummy.core.data.local.entity.RemoteKeys
import dev.haqim.productdummy.core.data.mapper.toEntity
import dev.haqim.productdummy.core.data.mapper.toModel
import dev.haqim.productdummy.core.data.remote.response.ProductsResponse
import dev.haqim.productdummy.core.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.random.Random
import kotlin.random.nextInt

object DataDummy {
    fun productsResponse() = Gson().fromJson(JsonConverter.readJsonFile("products.json"), ProductsResponse::class.java)
    
    fun productResponseToFav() = productsResponse().products.map {
        val random = Random.nextInt(1..90)
        ProductWithFavoriteEntity(it.toEntity(), if (it.id <= random) ProductFavoriteEntity(it.id, it.id) else null)
    }
    
    fun productWithFavoriteEntity(isFavorite: Boolean = false) = ProductWithFavoriteEntity(
        productsResponse().products[0].toEntity(), 
        if(isFavorite) 
            ProductFavoriteEntity(productsResponse().products[0].id, productsResponse().products[0].id)
        else null
    ).toModel()

    fun productsWithFavoriteEntity() = productsResponse().products.map {
        ProductWithFavoriteEntity(
            it.toEntity(),null
        )
    }
    
    fun productsEntity() = listOf(
        ProductEntity(
            discountPercentage= 0.0,
            thumbnail = "https://i.dummyjson.com/data/products/1/thumbnail.jpg",
            images = "https://i.dummyjson.com/data/products/1/1.jpg,https://i.dummyjson.com/data/products/1/2.jpg,https://i.dummyjson.com/data/products/1/3.jpg,https://i.dummyjson.com/data/products/1/4.jpg,https://i.dummyjson.com/data/products/1/thumbnail.jpg",
            price = 549,
            rating= 4.69,
            description = "An apple mobile which is nothing like apple",
            id = 1,
            title = "iPhone 9",
            stock = 94,
            category = "smartphones",
            brand = "Apple"
        ),
        ProductEntity(
            discountPercentage= 0.0,
            thumbnail = "https://i.dummyjson.com/data/products/1/thumbnail.jpg",
            images = "https://i.dummyjson.com/data/products/1/1.jpg,https://i.dummyjson.com/data/products/1/2.jpg,https://i.dummyjson.com/data/products/1/3.jpg,https://i.dummyjson.com/data/products/1/4.jpg,https://i.dummyjson.com/data/products/1/thumbnail.jpg",
            price = 549,
            rating= 4.69,
            description = "An apple mobile which is nothing like apple",
            id = 2,
            title = "iPhone 10",
            stock = 94,
            category = "smartphones",
            brand = "Apple"
        ),
        ProductEntity(
            discountPercentage= 0.0,
            thumbnail = "https://i.dummyjson.com/data/products/1/thumbnail.jpg",
            images = "https://i.dummyjson.com/data/products/1/1.jpg,https://i.dummyjson.com/data/products/1/2.jpg,https://i.dummyjson.com/data/products/1/3.jpg,https://i.dummyjson.com/data/products/1/4.jpg,https://i.dummyjson.com/data/products/1/thumbnail.jpg",
            price = 549,
            rating= 4.69,
            description = "An apple mobile which is nothing like apple",
            id = 3,
            title = "iPhone 11",
            stock = 94,
            category = "smartphones",
            brand = "Apple"
        ),
        ProductEntity(
            discountPercentage= 0.0,
            thumbnail = "https://i.dummyjson.com/data/products/1/thumbnail.jpg",
            images = "https://i.dummyjson.com/data/products/1/1.jpg,https://i.dummyjson.com/data/products/1/2.jpg,https://i.dummyjson.com/data/products/1/3.jpg,https://i.dummyjson.com/data/products/1/4.jpg,https://i.dummyjson.com/data/products/1/thumbnail.jpg",
            price = 549,
            rating= 4.69,
            description = "An apple mobile which is nothing like apple",
            id = 4,
            title = "iPhone 12",
            stock = 94,
            category = "smartphones",
            brand = "Apple"
        )
    )
    
    fun favoriteProduct() = ProductFavoriteEntity(
        id = 1,
        productId = 1
    )

    fun remoteKeys() = listOf(
        RemoteKeys("1", null, 5),
        RemoteKeys("2", null, 2),
        RemoteKeys("3", null, 3),
        RemoteKeys("4", null, 4)
    )

    fun pagingDataProductsFlow(showEmpty: Boolean = false): Flow<PagingData<Product>> {
        val dummyStoriesResponse = if (!showEmpty) productsWithFavoriteEntity() else listOf()
        val data: PagingData<ProductWithFavoriteEntity> = ProductRemoteMediatorMock.snapshot(dummyStoriesResponse)
        return flowOf(data).map {
            it.map { productEntity -> productEntity.toModel() }
        }
    }
}