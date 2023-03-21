package dev.haqim.productdummy.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.haqim.productdummy.data.local.entity.ProductEntity
import dev.haqim.productdummy.data.local.entity.ProductFavoriteEntity
import dev.haqim.productdummy.data.local.entity.RemoteKeys


@Database(
    entities = [RemoteKeys::class, ProductEntity::class, ProductFavoriteEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ProductDatabase: RoomDatabase() {
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun productDao(): ProductDao
    
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