package com.thrashspeed.gamecore

import android.content.Context
import com.thrashspeed.gamecore.data.access.local.repositories.GamesRepository
import com.thrashspeed.gamecore.data.access.local.repositories.ListsRepository
import com.thrashspeed.gamecore.db.GameCoreDatabase

object DependencyContainer {
    lateinit var db:GameCoreDatabase
        private set

    val gamesRepository by lazy {
        GamesRepository(
            gameDao = db.gameDao()
        )
    }

    val listsRepository by lazy {
        ListsRepository(
            listDao = db.listDao()
        )
    }

    fun provide(context: Context) {
        db = GameCoreDatabase.getInstance(context)
    }
}