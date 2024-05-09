package com.thrashspeed.gamecore.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.thrashspeed.gamecore.R

@Composable
fun ProfileScreen(topLevelNavController: NavController, navController: NavController) {
    ProfileScreenBodyContent(topLevelNavController = topLevelNavController, navController = navController)
}

@Composable
fun ProfileScreenBodyContent(topLevelNavController: NavController, navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE)

    Column (
        modifier = Modifier.padding(16.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = R.drawable.ic_launcher_foreground,
                contentDescription = "profile image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .width(90.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = prefs.getString("username", "").toString())
                Text(text = prefs.getString("email", "").toString())
            }
        }
    }
}
