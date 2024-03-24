package com.thrashspeed.gamecore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.thrashspeed.gamecore.navigation.AppNavigation
import com.thrashspeed.gamecore.screens.AuthScreen
import com.thrashspeed.gamecore.ui.theme.GameCoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameCoreTheme {
                AuthScreen()
            }
        }
    }
}
