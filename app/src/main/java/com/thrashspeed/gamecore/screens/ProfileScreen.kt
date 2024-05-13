package com.thrashspeed.gamecore.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.db.GameCoreDatabase
import com.thrashspeed.gamecore.firebase.FirebaseInstances
import com.thrashspeed.gamecore.navigation.AppScreens
import com.thrashspeed.gamecore.utils.composables.AcceptDenyDialog
import com.thrashspeed.gamecore.utils.composables.InfoAlert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(topLevelNavController: NavController) {
    ProfileScreenBodyContent(topLevelNavController = topLevelNavController)
}

@Composable
fun ProfileScreenBodyContent(topLevelNavController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE)
    var showLogOutDialog by remember { mutableStateOf(false) }
    var showResetPasswordDialog by remember { mutableStateOf(false) }
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

    if (showResetPasswordDialog) {
        InfoAlert(dialogTitleText = "Change password", dialogContentText = "A password reset link has been sent to your email: ${FirebaseInstances.authInstance.currentUser?.email}") {
            showResetPasswordDialog = false
        }
    }

    Column (
        modifier = Modifier.padding(16.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = R.drawable.gamecore_logo,
                contentDescription = "profile image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .width(90.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = prefs.getString("username", "").toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = prefs.getString("email", "").toString())
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.LockReset, contentDescription = "Password reset icon", modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = {
                        resetPassword(FirebaseInstances.authInstance.currentUser?.email.toString()) { success ->
                            if (!success) {
                                Toast.makeText(context, "Error when sending reset password email, please try again!", Toast.LENGTH_LONG).show()
                            } else {
                                showResetPasswordDialog = true
                            }
                        }
                    },
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f)
                    ) {
                    Text(text = "Change password", fontSize = 18.sp)
                }
            }

            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout icon", modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = {
                        showLogOutDialog = true
                    },
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.onErrorContainer),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Logout", fontSize = 18.sp)
                }
            }
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

// Function to reset password for a user with the specified email address
fun resetPassword(email: String, callback: (Boolean) -> Unit) {
    val auth = FirebaseInstances.authInstance

    auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            callback.invoke(task.isSuccessful)
        }
}