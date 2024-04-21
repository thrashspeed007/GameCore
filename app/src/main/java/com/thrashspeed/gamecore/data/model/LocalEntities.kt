package com.thrashspeed.gamecore.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val genre: String,
    val coverImageUrl: String,
    val status: GameStatus
)

enum class GameStatus {
    NOW_PLAYING,
    COMPLETED,
    TO_PLAY
}

