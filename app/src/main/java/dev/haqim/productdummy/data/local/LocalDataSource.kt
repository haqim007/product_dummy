package dev.haqim.productdummy.data.local

import androidx.room.withTransaction
import dev.haqim.productdummy.data.local.entity.ProductEntity
import dev.haqim.productdummy.data.local.entity.ProductFavoriteEntity
import dev.haqim.productdummy.data.local.entity.RemoteKeys
import dev.haqim.productdummy.data.local.room.ProductDatabase

class LocalDataSource private constructor(private val database: ProductDatabase){

    private val remoteKeysDao = database.remoteKeysDao()
    private val productDao = database.productDao()

    suspend fun clearRemoteKeys() = remoteKeysDao.clearRemoteKeys()
    suspend fun insertRemoteKeys(keys: List<RemoteKeys>) = remoteKeysDao.insertAll(keys)
    suspend fun getRemoteKeysById(id: String) = remoteKeysDao.getRemoteKeyById(id)

    fun getAllProducts() = productDao.getAllProducts()
    fun getAllFavoriteProducts() = productDao.getAllFavoriteProducts()
    fun getProductById(id: Int) = productDao.getProductById(id)
    suspend fun clearAllProducts() = productDao.clearAllProducts()
    suspend fun insertAllProducts(products: List<ProductEntity>) = productDao.insertProducts(products)
    suspend fun insertFavoriteProduct(product: ProductFavoriteEntity) = productDao.insertFavoriteProduct(product)
    suspend fun removeFavoriteProduct(productId: Int) = productDao.removeFavoriteProduct(productId)

    suspend fun insertKeysAndProducts(
        keys: List<RemoteKeys>,
        stories: List<ProductEntity>,
        isRefresh: Boolean = false
    ){
        database.withTransaction {
            if (isRefresh) {
                clearRemoteKeys()
                clearAllProducts()
            }
            insertRemoteKeys(keys)
            insertAllProducts(stories)
        }
    }
    
    companion object{
        private var INSTANCE: LocalDataSource? = null

        fun getInstance(database: ProductDatabase) = INSTANCE ?: synchronized(this){
            INSTANCE ?: LocalDataSource(database)
        }
    }
}