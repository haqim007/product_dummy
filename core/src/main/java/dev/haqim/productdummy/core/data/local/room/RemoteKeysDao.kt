package dev.haqim.productdummy.core.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey:List<dev.haqim.productdummy.core.data.local.entity.RemoteKeys>)

    @Query("SELECT * FROM remote_keys where id = :id")
    suspend fun getRemoteKeyById(id: String): dev.haqim.productdummy.core.data.local.entity.RemoteKeys?

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}