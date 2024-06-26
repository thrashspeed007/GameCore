package com.thrashspeed.gamecore.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.thrashspeed.gamecore.data.access.local.dao.GameDao
import com.thrashspeed.gamecore.data.access.local.dao.ListDao
import com.thrashspeed.gamecore.data.model.GameEntity
import com.thrashspeed.gamecore.data.model.ListEntity

@Database(entities = [GameEntity::class, ListEntity::class], version = 1, exportSchema = false)
abstract class GameCoreDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao
    abstract fun listDao(): ListDao

    companion object {
        @Volatile
        private var instance: GameCoreDatabase? = null

        fun getInstance(context: Context): GameCoreDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): GameCoreDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                GameCoreDatabase::class.java,
                "game_core_database"
            ).build()
        }
    }
}
