package com.thrashspeed.gamecore.screens.viewmodels

import androidx.lifecycle.ViewModel
import com.thrashspeed.gamecore.DependencyContainer
import com.thrashspeed.gamecore.data.access.local.repositories.ListsRepository

class ListsViewModel (private val listsRepository: ListsRepository = DependencyContainer.listsRepository) : ViewModel() {

}