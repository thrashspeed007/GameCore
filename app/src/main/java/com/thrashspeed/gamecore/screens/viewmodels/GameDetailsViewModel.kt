package com.thrashspeed.gamecore.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thrashspeed.gamecore.DependencyContainer
import com.thrashspeed.gamecore.data.access.igdb.GamesAccess
import com.thrashspeed.gamecore.data.access.local.repositories.GamesRepository
import com.thrashspeed.gamecore.data.model.GameDetailed
import com.thrashspeed.gamecore.data.model.GameEntity
import com.thrashspeed.gamecore.data.model.GameStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GameDetailsViewModel(gameId: Int) : ViewModel() {
    private val gamesAccess = GamesAccess()
    private val gamesRepository: GamesRepository = DependencyContainer.gamesRepository

    private val _gameDetails = MutableStateFlow<List<GameDetailed>>(emptyList())
    val gameDetails = _gameDetails

    init {
        fetchGameDetailsById(gameId)
    }

    private fun fetchGameDetailsById(id: Int) {
        viewModelScope.launch {
            gamesAccess.getGameDetails(id) { game ->
                _gameDetails.value = game
            }
        }
    }

    fun insertGame(status: GameStatus) {
        val game: GameDetailed = gameDetails.value.first()
        val gameEntity = GameEntity (
            gameId = game.id,
            name = game.name,
            releaseDate = game.first_release_date,
            coverImageUrl = game.cover.image_id,
            status = status
        )

        viewModelScope.launch {
            gamesRepository.insertGame(gameEntity)
        }
    }
}