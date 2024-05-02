package com.thrashspeed.gamecore.screens.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thrashspeed.gamecore.DependencyContainer
import com.thrashspeed.gamecore.data.access.local.repositories.ListsRepository
import com.thrashspeed.gamecore.data.model.GameItem
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

    fun addGameToList(game: GameItem, listId: Long) {
        viewModelScope.launch {
            val list = allLists.value?.find { it.id == listId }
            val updatedList = list?.copy(games = list.games.toMutableList().apply { add(game) })
            if (updatedList != null) {
                listsRepository.updateList(updatedList)
            }
        }
    }

    fun deleteGameFromList(game: GameItem, list: ListEntity) {
        viewModelScope.launch {
            val updatedList = list.copy(games = list.games.toMutableList().apply { remove(game) })
            listsRepository.updateList(updatedList)
        }
    }
}