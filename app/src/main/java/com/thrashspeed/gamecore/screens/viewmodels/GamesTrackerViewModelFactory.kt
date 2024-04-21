package com.thrashspeed.gamecore.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thrashspeed.gamecore.data.access.local.repositories.GameRepository

class GamesTrackerViewModelFactory(private val gameRepository: GameRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GamesTrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GamesTrackerViewModel(gameRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
