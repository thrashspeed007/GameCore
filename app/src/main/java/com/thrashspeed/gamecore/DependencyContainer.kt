package com.thrashspeed.gamecore

import android.content.Context
import com.thrashspeed.gamecore.data.access.local.repositories.GameRepository
import com.thrashspeed.gamecore.db.GameCoreDatabase

object DependencyContainer {
    lateinit var db:GameCoreDatabase
        private set

    val gamesRepository by lazy {
        GameRepository(
            gameDao = db.gameDao()
        )
    }

    fun provide(context: Context) {
        db = GameCoreDatabase.getInstance(context)
    }
}