package com.thrashspeed.gamecore.data.access.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.thrashspeed.gamecore.data.model.GameEntity
import com.thrashspeed.gamecore.data.model.GameStatus

@Dao
interface GameDao {
    @Query("SELECT * FROM games")
    fun getAllGames(): LiveData<List<GameEntity>>

    @Query("SELECT * FROM games WHERE status = :status")
    fun getGamesByStatus(status: GameStatus): LiveData<List<GameEntity>>

    @Insert
    suspend fun insertGame(game: GameEntity)

    @Delete
    suspend fun deleteGame(game: GameEntity)

    @Update
    suspend fun updateGame(game: GameEntity)
}
