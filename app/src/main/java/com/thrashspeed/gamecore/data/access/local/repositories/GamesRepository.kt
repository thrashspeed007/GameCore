package com.thrashspeed.gamecore.data.access.local.repositories

import androidx.lifecycle.LiveData
import com.thrashspeed.gamecore.data.access.local.dao.GameDao
import com.thrashspeed.gamecore.data.model.GameEntity
import com.thrashspeed.gamecore.data.model.GameStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GamesRepository(private val gameDao: GameDao) {

    fun getAllGames(): LiveData<List<GameEntity>> {
        return gameDao.getAllGames()
    }

    fun getGamesByStatus(status: GameStatus): LiveData<List<GameEntity>> {
        return gameDao.getGamesByStatus(status)
    }

    fun getGameById(gameId: Long): LiveData<GameEntity> {
        return gameDao.getGameById(gameId)
    }

    suspend fun insertGame(game: GameEntity) {
        withContext(Dispatchers.IO) {
            gameDao.insertGame(game)
        }
    }

    suspend fun deleteGame(game: GameEntity) {
        withContext(Dispatchers.IO) {
            gameDao.deleteGame(game)
        }
    }

    suspend fun updateGame(game: GameEntity) {
        withContext(Dispatchers.IO) {
            gameDao.updateGame(game)
        }
    }
}