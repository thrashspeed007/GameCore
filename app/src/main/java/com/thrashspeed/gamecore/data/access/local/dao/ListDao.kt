package com.thrashspeed.gamecore.data.access.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.thrashspeed.gamecore.data.model.ListEntity

@Dao
interface ListDao {
    @Query("SELECT * FROM lists")
    fun getAllLists(): LiveData<List<ListEntity>>

    @Insert
    suspend fun insertList(list: ListEntity)

    @Update
    suspend fun updateList(list: ListEntity)

    @Delete
    suspend fun deleteList(list: ListEntity)
}