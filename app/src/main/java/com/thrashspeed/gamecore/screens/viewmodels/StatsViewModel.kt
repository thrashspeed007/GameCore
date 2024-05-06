package com.thrashspeed.gamecore.screens.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.thrashspeed.gamecore.DependencyContainer
import com.thrashspeed.gamecore.data.access.local.repositories.GamesRepository
import com.thrashspeed.gamecore.data.model.GameEntity

class StatsViewModel(private val gamesRepository: GamesRepository = DependencyContainer.gamesRepository): ViewModel() {
    val allGames: LiveData<List<GameEntity>> = gamesRepository.getAllGames()
}