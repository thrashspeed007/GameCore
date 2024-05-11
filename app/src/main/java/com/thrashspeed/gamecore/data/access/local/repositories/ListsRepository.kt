package com.thrashspeed.gamecore.data.access.local.repositories

import androidx.lifecycle.LiveData
import com.thrashspeed.gamecore.data.access.local.dao.ListDao
import com.thrashspeed.gamecore.data.model.ListEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ListsRepository(private val listDao: ListDao) {
    fun getAllLists(): LiveData<List<ListEntity>> {
        return listDao.getAllLists()
    }

    suspend fun insertList(list: ListEntity): Long {
        return listDao.insertList(list)
    }

    suspend fun importLists(lists: List<ListEntity>) {
        for (list in lists) {
            listDao.insertList(list)
        }
    }

    suspend fun updateList(list: ListEntity) {
        withContext(Dispatchers.IO) {
            listDao.updateList(list)
        }
    }

    suspend fun deleteList(list: ListEntity) {
        withContext(Dispatchers.IO) {
            listDao.deleteList(list)
        }
    }
}