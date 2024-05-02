package com.thrashspeed.gamecore.screens.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thrashspeed.gamecore.DependencyContainer
import com.thrashspeed.gamecore.data.access.local.repositories.GamesRepository
import com.thrashspeed.gamecore.data.model.GameEntity
import com.thrashspeed.gamecore.data.model.GameStatus
import kotlinx.coroutines.launch

class GamesTrackerViewModel (private val gamesRepository: GamesRepository = DependencyContainer.gamesRepository) : ViewModel() {
    val allGames: LiveData<List<GameEntity>> = gamesRepository.getAllGames()

    fun getGamesByStatus(status: GameStatus): LiveData<List<GameEntity>> {
        return gamesRepository.getGamesByStatus(status)
    }

    fun insertGame(game: GameEntity) {
        viewModelScope.launch {
            gamesRepository.insertGame(game)
        }
    }

    fun deleteGame(game: GameEntity) {
        viewModelScope.launch {
            gamesRepository.deleteGame(game)
        }
    }

    fun changeGameStatus(game: GameEntity, status: GameStatus) {
        viewModelScope.launch {
            game.status = status
            gamesRepository.updateGame(game)
        }
    }
}