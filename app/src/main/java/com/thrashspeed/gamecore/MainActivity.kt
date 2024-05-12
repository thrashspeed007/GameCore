package com.thrashspeed.gamecore

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.thrashspeed.gamecore.navigation.AppNavigation
import com.thrashspeed.gamecore.screens.AuthScreen
import com.thrashspeed.gamecore.ui.theme.GameCoreTheme

class MainActivity : ComponentActivity() {

    private val darkThemeCallback: (Boolean) -> Unit =
        { window.decorView.setBackgroundColor(if (it) 0xFF1C1B1F.toInt() else 0xFFFFFBFE.toInt()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DependencyContainer.provide(this)

        setContent {
            GameCoreTheme {
                if (session()) {
                    AppNavigation(darkThemeCallback)
                } else {
                    AuthScreen(::onAuthSuccess)
                }
            }
        }
    }

    private fun onAuthSuccess() {
        setContent {
            GameCoreTheme {
                AppNavigation(darkThemeCallback)
            }
        }
    }

    private fun session(): Boolean {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        return email != null
    }
}