package com.thrashspeed.gamecore.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thrashspeed.gamecore.data.access.GamesAccess
import com.thrashspeed.gamecore.data.model.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ExploreViewModel : ViewModel() {
    private val gamesAccess = GamesAccess()

    // State to hold the list of popular games
    private val _popularGames = MutableStateFlow<List<Game>>(emptyList())
    val popularGames = _popularGames

    init {
        // Fetch popular games when the ViewModel is initialized
        fetchFamousGames()
    }

    private fun fetchFamousGames() {
        viewModelScope.launch {
            gamesAccess.getFamousGames { games ->
                _popularGames.value = games
            }
        }
    }
}