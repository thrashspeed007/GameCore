package com.thrashspeed.gamecore.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.db.GameCoreDatabase
import com.thrashspeed.gamecore.firebase.FirebaseInstances
import com.thrashspeed.gamecore.navigation.AppScreens
import com.thrashspeed.gamecore.utils.composables.AcceptDenyDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(topLevelNavController: NavController, navController: NavController) {
    ProfileScreenBodyContent(topLevelNavController = topLevelNavController, navController = navController)
}

@Composable
fun ProfileScreenBodyContent(topLevelNavController: NavController, navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE)
    var showLogOutDialog by remember { mutableStateOf(false) }
    val coroutineScope  = rememberCoroutineScope()

    if (showLogOutDialog) {
        AcceptDenyDialog(dialogTitleText = "Log out", dialogContentText = "Are you sure you want to log out?") { accept ->
            if (accept) {
                coroutineScope.launch {
                    val prefsEdit = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                    prefsEdit.clear()
                    prefsEdit.apply()
                    FirebaseInstances.authInstance.signOut()

                    deleteAllRoomData(context)

                    topLevelNavController.navigate(AppScreens.AuthScreen.route)
                    showLogOutDialog = false
                }
            }
        }
    }

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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                showLogOutDialog = true
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Logout")
        }
    }
}

// Function to delete all room data
suspend fun deleteAllRoomData(context: Context) {
    withContext(Dispatchers.IO) {
        val db = GameCoreDatabase.getInstance(context)
        db.clearAllTables()
    }
}