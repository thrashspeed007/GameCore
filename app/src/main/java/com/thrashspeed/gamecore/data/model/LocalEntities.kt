package com.thrashspeed.gamecore.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameId: Long,
    val name: String,
    val releaseDate: Long,
    val coverImageUrl: String,
    var status: GameStatus,
    var sessionStartedTempDate: Long = 0,
    var lastSessionTimePlayed: Long = 0,
    var timePlayed: Long = 0,
    var firstDayOfPlay: Long = 0,
    val dayOfCompletion: Long = 0
)

@Entity(tableName = "lists")
@TypeConverters(GameItemConverter::class)
data class ListEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val games: List<GameItem>
)

enum class GameStatus(val displayName: String) {
    NOW_PLAYING("Playing"),
    COMPLETED("Completed"),
    TO_PLAY("To Play");
}

class GameItemConverter {
    @TypeConverter
    fun fromGameItemList(gameItemList: List<GameItem>): String {
        val gson = Gson()
        return gson.toJson(gameItemList)
    }

    @TypeConverter
    fun toGameItemList(data: String): List<GameItem> {
        val listType = object : TypeToken<List<GameItem>>() {}.type
        return Gson().fromJson(data, listType)
    }
}

