package com.thrashspeed.gamecore.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thrashspeed.gamecore.data.access.GamesAccess
import com.thrashspeed.gamecore.data.model.GameDetailed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GameDetailsViewModel(gameId: Int) : ViewModel() {
    private val gamesAccess = GamesAccess()

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
}