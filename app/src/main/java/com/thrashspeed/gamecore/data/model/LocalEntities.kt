package com.thrashspeed.gamecore.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val releaseDate: Long,
    val coverImageUrl: String,
    val genres: String,
    var status: GameStatus,
    var sessionStartedTempDate: Long = 0,
    var lastSessionTimePlayed: Long = 0,
    var timePlayed: Long = 0,
    var firstDayOfPlay: Long = 0,
    var dayOfCompletion: Long = 0
) {
    fun toMap(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        val properties = GameEntity::class.memberProperties

        for (prop in properties) {
            prop.isAccessible = true
            val value = prop.get(this)

            // Check if the property is an enum
            if (value is GameStatus) {
                // Map the display name of the enum value
                val displayName = (value.name)
                map[prop.name] = displayName
            } else {
                map[prop.name] = value
            }
        }

        return map
    }
}

@Entity(tableName = "lists")
@TypeConverters(GameItemConverter::class)
data class ListEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val games: List<GameItem>
) {
    fun toMap(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        val properties = ListEntity::class.memberProperties

        for (prop in properties) {
            prop.isAccessible = true
            val value = prop.get(this)
            if (prop.name == "games" && value is List<*>) {
                val games = mutableListOf<Map<String, Any?>>()
                value.forEach { item ->
                    if (item is GameItem) {
                        games.add(gameItemToMap(item))
                    }
                }
                map[prop.name] = games
            } else {
                map[prop.name] = value
            }
        }

        return map
    }

}

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

private fun gameItemToMap(gameItem: GameItem): Map<String, Any?> {
    return mapOf(
        "id" to gameItem.id,
        "name" to gameItem.name,
        "cover" to gameItem.cover,
        "first_release_date" to gameItem.first_release_date
    )
}

