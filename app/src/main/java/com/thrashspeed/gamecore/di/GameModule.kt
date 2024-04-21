package com.thrashspeed.gamecore.di

import android.content.Context
import androidx.room.Room
import com.thrashspeed.gamecore.data.access.local.dao.GameDao
import com.thrashspeed.gamecore.data.access.local.repositories.GameRepository
import com.thrashspeed.gamecore.db.GameDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object GameModule {

    @Provides
    fun provideGameDatabase(@ApplicationContext context: Context): GameDatabase {
        return Room.databaseBuilder(
            context,
            GameDatabase::class.java,
            "game_database"
        ).build()
    }

    @Provides
    fun provideGameDao(gameDatabase: GameDatabase): GameDao {
        return gameDatabase.gameDao()
    }

    @Provides
    fun provideGameRepository(gameDao: GameDao): GameRepository {
        return GameRepository(gameDao)
    }
}
