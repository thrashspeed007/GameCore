package com.thrashspeed.gamecore.screens.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thrashspeed.gamecore.DependencyContainer
import com.thrashspeed.gamecore.data.access.local.repositories.ListsRepository
import com.thrashspeed.gamecore.data.model.GameItem
import com.thrashspeed.gamecore.data.model.ListEntity
import com.thrashspeed.gamecore.firebase.firestore.FirestoreRepository
import kotlinx.coroutines.launch

class ListsViewModel (private val listsRepository: ListsRepository = DependencyContainer.listsRepository) : ViewModel() {
    val allLists: LiveData<List<ListEntity>> = listsRepository.getAllLists()

    fun insertList(list: ListEntity) {
        viewModelScope.launch {
            val listId = listsRepository.insertList(list)
            val firestoreList = list.copy(id = listId)
            FirestoreRepository.insertList(firestoreList)
        }
    }

    fun updateListInfo(list: ListEntity) {
        viewModelScope.launch {
            val fieldsToUpdate = hashMapOf<String, Any>()
            fieldsToUpdate["name"] = list.name
            fieldsToUpdate["description"] = list.description

            listsRepository.updateList(list)
            FirestoreRepository.updateList(list.id, fieldsToUpdate)
        }
    }

    fun deleteList(list: ListEntity) {
        viewModelScope.launch {
            listsRepository.deleteList(list)
            FirestoreRepository.deleteList(list)
        }
    }

    fun addGameToList(game: GameItem, listId: Long) {
        viewModelScope.launch {
            val fieldsToUpdate = hashMapOf<String, Any>()

            val list = allLists.value?.find { it.id == listId }
            val updatedList = list?.copy(games = list.games.toMutableList().apply { add(game) })
            if (updatedList != null) {
                fieldsToUpdate["games"] = updatedList.games
            }
            if (updatedList != null) {
                listsRepository.updateList(updatedList)
                FirestoreRepository.updateList(listId, fieldsToUpdate)
            }
        }
    }

    fun deleteGameFromList(game: GameItem, list: ListEntity) {
        viewModelScope.launch {
            val fieldsToUpdate = hashMapOf<String, Any>()

            val updatedList = list.copy(games = list.games.toMutableList().apply { remove(game) })
            fieldsToUpdate["games"] = updatedList.games
            listsRepository.updateList(updatedList)
            FirestoreRepository.updateList(list.id, fieldsToUpdate)
        }
    }
}