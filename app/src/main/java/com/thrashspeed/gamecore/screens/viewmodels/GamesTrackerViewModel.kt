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
import com.thrashspeed.gamecore.firebase.firestore.FirestoreRepository
import kotlinx.coroutines.launch

class GamesTrackerViewModel (private val gamesRepository: GamesRepository = DependencyContainer.gamesRepository) : ViewModel() {
    val isSessionActive = mutableStateOf(false)
    val gameInSessionId = mutableLongStateOf(-1L)

    fun getGamesByStatus(status: GameStatus): LiveData<List<GameEntity>> {
        return gamesRepository.getGamesByStatus(status)
    }

    fun updateGame(game: GameEntity) {
        viewModelScope.launch {
            gamesRepository.updateGame(game)
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
            FirestoreRepository.deleteGame(game) { success ->
                if (!success) {
                    Toast.makeText(context, "Failed to delete the game in the cloud!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun changeGameStatus(context: Context, game: GameEntity, status: GameStatus) {
        viewModelScope.launch {
            val fieldsToUpdate = hashMapOf<String, String>()

            if (status == GameStatus.COMPLETED) {
                game.dayOfCompletion = System.currentTimeMillis()
                fieldsToUpdate["dayOfCompletion"] = game.dayOfCompletion.toString()
            }
            game.status = status
            fieldsToUpdate["status"] = status.displayName

            gamesRepository.updateGame(game)
            FirestoreRepository.updateGame(game.id, fieldsToUpdate) { success ->
                if (!success) {
                    Toast.makeText(context, "Failed to update the game in the cloud!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun startSession(context: Context, game: GameEntity) {
        viewModelScope.launch {
            val fieldsToUpdate = hashMapOf<String, String>()

            val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.putBoolean("isSessionActive", true)
            prefs.apply()
            isSessionActive.value = true

            game.sessionStartedTempDate = System.currentTimeMillis()
            fieldsToUpdate["sessionStartedTempDate"] = game.sessionStartedTempDate.toString()
            if (game.firstDayOfPlay == 0L) {
                game.firstDayOfPlay = game.sessionStartedTempDate
                fieldsToUpdate["firstDayOfPlay"] = game.firstDayOfPlay.toString()
            }
            gamesRepository.updateGame(game)
            FirestoreRepository.updateGame(game.id, fieldsToUpdate) { success ->
                if (!success) {
                    Toast.makeText(context, "Failed to update the game in the cloud!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun endSession(context: Context, game: GameEntity, currentTime: Long) {
        viewModelScope.launch {
            val fieldsToUpdate = hashMapOf<String, String>()

            val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.putBoolean("isSessionActive", false)
            prefs.apply()
            isSessionActive.value = false

            game.lastSessionTimePlayed = currentTime
            fieldsToUpdate["lastSessionTimePlayed"] = game.lastSessionTimePlayed.toString()
            game.timePlayed += game.lastSessionTimePlayed
            fieldsToUpdate["timePlayed"] = game.timePlayed.toString()
            game.sessionStartedTempDate = 0L
            fieldsToUpdate["sessionStartedTempDate"] = "0"
            gamesRepository.updateGame(game)
            FirestoreRepository.updateGame(game.id, fieldsToUpdate) { success ->
                if (!success) {
                    Toast.makeText(context, "Failed to update the game in the cloud!", Toast.LENGTH_SHORT).show()
                }
            }

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