package com.thrashspeed.gamecore.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameId: Long,
    val name: String,
    val releaseDate: Long,
    val coverImageUrl: String,
    val status: GameStatus
)

enum class GameStatus(val displayName: String) {
    NOW_PLAYING("Playing"),
    COMPLETED("Completed"),
    TO_PLAY("To Play");
}

