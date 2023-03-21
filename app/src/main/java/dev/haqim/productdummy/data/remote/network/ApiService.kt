package dev.haqim.productdummy.data.remote.network

import dev.haqim.productdummy.data.remote.response.ProductsResponse
import retrofit2.http.*

interface ApiService {
    @GET("products")
    suspend fun getProducts(@Query("size") size: Int, @Query("skip") skip: Int): ProductsResponse
}