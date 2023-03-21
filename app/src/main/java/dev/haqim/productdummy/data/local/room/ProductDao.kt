package dev.haqim.productdummy.data.local.room

import androidx.paging.PagingSource
import androidx.room.*
import dev.haqim.productdummy.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteProduct(product: ProductFavoriteEntity)

    @Query("DELETE FROM $TABLE_PRODUCTS_FAVORITE WHERE product_id = :productId")
    suspend fun removeFavoriteProduct(productId: Int)
    
    @Transaction
    @Query("SELECT * FROM $TABLE_PRODUCTS")
    fun getAllProducts(): PagingSource<Int, ProductWithFavoriteEntity>

    @Transaction
    @Query("SELECT * FROM $TABLE_PRODUCTS WHERE id IN (SELECT product_id FROM $TABLE_PRODUCTS_FAVORITE)")
    fun getAllFavoriteProducts(): PagingSource<Int, ProductWithFavoriteEntity>

    @Transaction
    @Query("SELECT * FROM $TABLE_PRODUCTS where id = :id")
    fun getProductById(id: Int): Flow<ProductWithFavoriteEntity?>
    
    @Query("DELETE FROM $TABLE_PRODUCTS")
    suspend fun clearAllProducts()
}