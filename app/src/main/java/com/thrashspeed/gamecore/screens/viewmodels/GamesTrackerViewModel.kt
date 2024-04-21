package com.thrashspeed.gamecore.screens.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thrashspeed.gamecore.DependencyContainer
import com.thrashspeed.gamecore.data.access.local.repositories.GameRepository
import com.thrashspeed.gamecore.data.model.GameEntity
import com.thrashspeed.gamecore.data.model.GameStatus
import kotlinx.coroutines.launch

class GamesTrackerViewModel (private val gameRepository: GameRepository = DependencyContainer.gamesRepository) : ViewModel() {
    private val _selectedTabIndex = mutableIntStateOf(0)
    val selectedTabIndex: State<Int> = _selectedTabIndex

    fun setSelectedTabIndex(index: Int) {
        _selectedTabIndex.intValue = index
    }


    val allGames: LiveData<List<GameEntity>> = gameRepository.getAllGames()

    fun getGamesByStatus(status: GameStatus): LiveData<List<GameEntity>> {
        return gameRepository.getGamesByStatus(status)
    }

    fun insertGame(game: GameEntity) {
        viewModelScope.launch {
            gameRepository.insertGame(game)
        }
    }

    fun deleteGame(game: GameEntity) {
        viewModelScope.launch {
            gameRepository.deleteGame(game)
        }
    }
}