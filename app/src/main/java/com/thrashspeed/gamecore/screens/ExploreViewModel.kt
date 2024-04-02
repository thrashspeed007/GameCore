package com.thrashspeed.gamecore.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thrashspeed.gamecore.data.access.GamesAccess
import com.thrashspeed.gamecore.data.model.GameItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ExploreViewModel : ViewModel() {
    private val _selectedTabIndex = mutableIntStateOf(0)
    val selectedTabIndex: State<Int> = _selectedTabIndex

    fun setSelectedTabIndex(index: Int) {
        _selectedTabIndex.intValue = index
    }

    private val gamesAccess = GamesAccess()

    // State to hold the list of popular games
    private val _popularGames = MutableStateFlow<List<GameItem>>(emptyList())
    private val _trendingGames = MutableStateFlow<List<GameItem>>(emptyList())
    val popularGames = _popularGames
    val trendingGames = _trendingGames

    init {
        fetchFamousGames()
        fetchTrendingGames()
    }

    private fun fetchFamousGames() {
        viewModelScope.launch {
            gamesAccess.getFamousGames { games ->
                _popularGames.value = games
            }
        }
    }

    private fun fetchTrendingGames() {
        viewModelScope.launch {
            gamesAccess.getTrendingGames { games ->
                _trendingGames.value = games
            }
        }
    }
}