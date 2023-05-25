package dev.haqim.productdummy.core.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.haqim.productdummy.core.data.local.entity.ProductEntity
import dev.haqim.productdummy.core.data.local.entity.ProductFavoriteEntity
import dev.haqim.productdummy.core.data.local.entity.RemoteKeys


@Database(
    entities = [RemoteKeys::class, ProductEntity::class, ProductFavoriteEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ProductDatabase: RoomDatabase() {
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun productDao(): ProductDao
    
}