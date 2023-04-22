package dev.haqim.productdummy.core.data.local

import androidx.paging.PagingSource
import dev.haqim.productdummy.core.data.local.entity.ProductEntity
import dev.haqim.productdummy.core.data.local.entity.ProductFavoriteEntity
import dev.haqim.productdummy.core.data.local.entity.ProductWithFavoriteEntity
import dev.haqim.productdummy.core.data.local.room.ProductDao
import dev.haqim.productdummy.core.util.MemoryPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeProductDao: ProductDao {

    private var products = mutableListOf<ProductEntity>()
    private var favoriteProducts = mutableListOf<ProductFavoriteEntity>()
    private val productWithFavorite
        get() = products.map { product ->
                ProductWithFavoriteEntity(product, favoriteProducts.find { it.productId == product.id })
            }
    private val productWithFavoriteOnly
        get() = productWithFavorite.filter { product -> product.isFavorite }
    private val favoriteProductFlow = MutableStateFlow<ProductWithFavoriteEntity?>(null)


    private fun updateSelectedFavProductById(productId: Int) {
        favoriteProductFlow.update {
            productWithFavorite.find { it.product.id == productId }
        }
    }

    override suspend fun insertProducts(products: List<ProductEntity>) {
        this.products.addAll(products)
    }

    override suspend fun insertFavoriteProduct(product: ProductFavoriteEntity) {
        this.favoriteProducts.add(product)
        this.favoriteProducts = this.favoriteProducts.toSet().toMutableList()

        updateSelectedFavProductById(product.productId)
    }

    override suspend fun removeFavoriteProduct(productId: Int) {
        this.favoriteProducts.removeIf { data ->
            data.productId == productId
        }

        updateSelectedFavProductById(productId)
    }

    override fun getAllProducts(): PagingSource<Int, ProductWithFavoriteEntity> {
        return MemoryPagingSource(productWithFavorite)
    }

    override fun getAllFavoriteProducts(): PagingSource<Int, ProductWithFavoriteEntity> {
        return MemoryPagingSource(productWithFavoriteOnly)
    }

    override fun getProductById(id: Int): Flow<ProductWithFavoriteEntity?> {
        updateSelectedFavProductById(id)
        
        return favoriteProductFlow.asStateFlow()
    }

    override suspend fun clearAllProducts() {
        products.clear()
        favoriteProducts.clear()
    }
}
