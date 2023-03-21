package dev.haqim.productdummy.core.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.haqim.productdummy.core.data.local.entity.ProductEntity
import dev.haqim.productdummy.core.data.local.entity.ProductFavoriteEntity
import dev.haqim.productdummy.core.data.local.entity.RemoteKeys


@Database(
    entities = [dev.haqim.productdummy.core.data.local.entity.RemoteKeys::class, dev.haqim.productdummy.core.data.local.entity.ProductEntity::class, dev.haqim.productdummy.core.data.local.entity.ProductFavoriteEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ProductDatabase: RoomDatabase() {
    abstract fun remoteKeysDao(): dev.haqim.productdummy.core.data.local.room.RemoteKeysDao
    abstract fun productDao(): dev.haqim.productdummy.core.data.local.room.ProductDao
    
//    companion object{
//        @Volatile
//        private var INSTANCE: ProductDatabase? = null
//        
//        @JvmStatic
//        fun getInstance(context: Context): ProductDatabase {
//            return INSTANCE ?: synchronized(this){
//                INSTANCE ?: Room.databaseBuilder(
//                    context.applicationContext,
//                    ProductDatabase::class.java,
//                    "product_dummy.db"
//                )
//                    .fallbackToDestructiveMigration()
//                    .build()
//                    .also { 
//                        INSTANCE = it
//                    }
//            }
//        }
//    }
    
}