package dev.haqim.productdummy.data.remote

import dev.haqim.productdummy.data.remote.network.ApiService
import dev.haqim.productdummy.data.remote.response.ProductsResponse

class RemoteDataSource(
    private val service: ApiService
) {
    suspend fun getProducts(size: Int, skip: Int): Result<ProductsResponse>{
        return try {
            Result.success(service.getProducts(size, skip))
        }catch (e: Exception){
            Result.failure(e)
        }
    }
}