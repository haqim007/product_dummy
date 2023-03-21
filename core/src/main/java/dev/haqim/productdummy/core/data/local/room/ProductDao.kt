package dev.haqim.productdummy.core.data.local.room

import androidx.paging.PagingSource
import androidx.room.*
import dev.haqim.productdummy.core.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<dev.haqim.productdummy.core.data.local.entity.ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteProduct(product: dev.haqim.productdummy.core.data.local.entity.ProductFavoriteEntity)

    @Query("DELETE FROM ${dev.haqim.productdummy.core.data.local.entity.TABLE_PRODUCTS_FAVORITE} WHERE product_id = :productId")
    suspend fun removeFavoriteProduct(productId: Int)
    
    @Transaction
    @Query("SELECT * FROM ${dev.haqim.productdummy.core.data.local.entity.TABLE_PRODUCTS}")
    fun getAllProducts(): PagingSource<Int, dev.haqim.productdummy.core.data.local.entity.ProductWithFavoriteEntity>

    @Transaction
    @Query("SELECT * FROM ${dev.haqim.productdummy.core.data.local.entity.TABLE_PRODUCTS} WHERE id IN (SELECT product_id FROM ${dev.haqim.productdummy.core.data.local.entity.TABLE_PRODUCTS_FAVORITE})")
    fun getAllFavoriteProducts(): PagingSource<Int, dev.haqim.productdummy.core.data.local.entity.ProductWithFavoriteEntity>

    @Transaction
    @Query("SELECT * FROM ${dev.haqim.productdummy.core.data.local.entity.TABLE_PRODUCTS} where id = :id")
    fun getProductById(id: Int): Flow<dev.haqim.productdummy.core.data.local.entity.ProductWithFavoriteEntity?>
    
    @Query("DELETE FROM ${dev.haqim.productdummy.core.data.local.entity.TABLE_PRODUCTS}")
    suspend fun clearAllProducts()
}