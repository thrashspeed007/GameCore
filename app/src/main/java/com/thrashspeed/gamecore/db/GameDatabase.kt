package com.thrashspeed.gamecore.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.thrashspeed.gamecore.data.access.local.dao.GameDao
import com.thrashspeed.gamecore.data.model.GameEntity

@Database(entities = [GameEntity::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var instance: GameDatabase? = null

        fun getInstance(context: Context): GameDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): GameDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                GameDatabase::class.java,
                "game_database"
            ).build()
        }
    }
}
