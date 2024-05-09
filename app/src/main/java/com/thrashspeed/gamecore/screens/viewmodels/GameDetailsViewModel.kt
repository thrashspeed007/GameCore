package com.thrashspeed.gamecore.screens.viewmodels

import android.database.SQLException
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thrashspeed.gamecore.DependencyContainer
import com.thrashspeed.gamecore.data.access.igdb.GamesAccess
import com.thrashspeed.gamecore.data.access.local.repositories.GamesRepository
import com.thrashspeed.gamecore.data.model.GameDetailed
import com.thrashspeed.gamecore.data.model.GameEntity
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

    suspend fun insertGame(gameEntity: GameEntity): Result<Unit> {
        return try {
            gamesRepository.insertGame(gameEntity)
            Result.Success(Unit)
        } catch (e: SQLiteConstraintException) {
            Result.Error("You already have this game!")
        } catch (e: SQLException) {
            Result.Error(e.message.toString())
        } catch (e: Exception) {
            Result.Error(e.toString())
        }
    }
}

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: String) : Result<Nothing>()
}
