package com.thrashspeed.gamecore.screens.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thrashspeed.gamecore.DependencyContainer
import com.thrashspeed.gamecore.data.access.local.repositories.ListsRepository
import com.thrashspeed.gamecore.data.model.ListEntity
import kotlinx.coroutines.launch

class ListsViewModel (private val listsRepository: ListsRepository = DependencyContainer.listsRepository) : ViewModel() {
    val allLists: LiveData<List<ListEntity>> = listsRepository.getAllLists()

    fun insertList(list: ListEntity) {
        viewModelScope.launch {
            listsRepository.insertList(list)
        }
    }

    fun deleteList(list: ListEntity) {
        viewModelScope.launch {
            listsRepository.deleteList(list)
        }
    }

}