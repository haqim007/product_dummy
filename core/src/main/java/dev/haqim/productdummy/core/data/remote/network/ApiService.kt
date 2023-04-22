package dev.haqim.productdummy.core.data.remote.network

import dev.haqim.productdummy.core.data.remote.response.ProductsResponse
import retrofit2.http.*

interface ApiService {
    @GET("products")
    suspend fun getProducts(@Query("limit") size: Int, @Query("skip") skip: Int): ProductsResponse
}