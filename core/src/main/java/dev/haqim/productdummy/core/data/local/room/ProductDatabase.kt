package dev.haqim.productdummy.core.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [dev.haqim.productdummy.core.data.local.entity.RemoteKeys::class, dev.haqim.productdummy.core.data.local.entity.ProductEntity::class, dev.haqim.productdummy.core.data.local.entity.ProductFavoriteEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ProductDatabase: RoomDatabase() {
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun productDao(): ProductDao
    
}