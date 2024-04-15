package com.thrashspeed.gamecore.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameDetailsViewModelFactory(private val gameId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameDetailsViewModel::class.java)) {
            return GameDetailsViewModel(gameId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

