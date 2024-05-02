package com.thrashspeed.gamecore.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(topLevelNavController: NavController, isDarkTheme: Boolean, onThemeChanged: (Boolean) -> Unit) {
    Scaffold {
        Column {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                IconButton(
                    onClick = { topLevelNavController.popBackStack() },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "GoBackIcon")
                }

                Text(
                    text = "Settings",
                    fontSize = 24.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column (
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Dark theme:", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Switch(checked = isDarkTheme, onCheckedChange = {
                        onThemeChanged.invoke(it)
                    })
                }
            }
        }
    }
}