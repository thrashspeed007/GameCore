package com.thrashspeed.gamecore.screens.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thrashspeed.gamecore.DependencyContainer
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.data.access.local.repositories.GamesRepository
import com.thrashspeed.gamecore.data.model.GameEntity
import com.thrashspeed.gamecore.data.model.GameStatus
import kotlinx.coroutines.launch

class GamesTrackerViewModel (private val gamesRepository: GamesRepository = DependencyContainer.gamesRepository) : ViewModel() {
    val isSessionActive = mutableStateOf(false)
    val gameInSessionId = mutableLongStateOf(-1L)

    fun getGamesByStatus(status: GameStatus): LiveData<List<GameEntity>> {
        return gamesRepository.getGamesByStatus(status)
    }

    fun insertGame(game: GameEntity) {
        viewModelScope.launch {
            gamesRepository.insertGame(game)
        }
    }

    fun getGameById(gameId: Long): LiveData<GameEntity> {
        return gamesRepository.getGameById(gameId)
    }

    fun deleteGame(context: Context, game: GameEntity) {
        if (gameInSessionId.longValue == game.id) {
            val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.putLong("gameInSessionId", -1L)
            prefs.apply()
//            gameInSessionId.longValue = -1L
        }

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

    fun startSession(context: Context, game: GameEntity) {
        viewModelScope.launch {
            game.sessionStartedTempDate = System.currentTimeMillis()
            if (game.firstDayOfPlay == 0L) {
                game.firstDayOfPlay = game.sessionStartedTempDate
            }
            gamesRepository.updateGame(game)

            val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.putBoolean("isSessionActive", true)
            prefs.apply()
            isSessionActive.value = true
        }
    }

    fun endSession(context: Context, game: GameEntity, currentTime: Long) {
        viewModelScope.launch {
            isSessionActive.value = false
            game.lastSessionTimePlayed = currentTime
            game.timePlayed += game.lastSessionTimePlayed
            game.sessionStartedTempDate = 0L
            gamesRepository.updateGame(game)

            val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.putBoolean("isSessionActive", false)
            prefs.apply()
        }
    }

    fun addGameToSession(context: Context, gameId: Long) {
        if (isSessionActive.value) {
            if (gameInSessionId.longValue != gameId) {
                Toast.makeText(context, "Stop active session!", Toast.LENGTH_SHORT).show()
            }
            return
        }
        val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putLong("gameInSessionId", gameId)
        prefs.apply()
        gameInSessionId.longValue = gameId
    }

    fun checkGameSession(context: Context) {
        val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE)
        gameInSessionId.longValue = prefs.getLong("gameInSessionId", -1L)
        isSessionActive.value = prefs.getBoolean("isSessionActive", false)
    }

    fun getSessionActiveTime(sessionStartedTempDate: Long): Long {
        if (sessionStartedTempDate == 0L) {
            return 0
        }

        return System.currentTimeMillis() - sessionStartedTempDate
    }

    fun formatDuration(milliseconds: Long, secondsIncluded: Boolean = false): String {
        val totalSeconds = milliseconds / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (secondsIncluded) {
            "$hours" + "h " + minutes + "m " + seconds + "s"
        } else {
            "$hours" + "h " + minutes + "m"
        }
    }


}