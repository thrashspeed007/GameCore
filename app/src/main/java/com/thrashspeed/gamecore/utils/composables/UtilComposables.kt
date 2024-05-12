package com.thrashspeed.gamecore.utils.composables

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ShowToast(context: Context, message: String, length: Int = 0) {
    Toast.makeText(context, message, length).show()
}

@Composable
fun DeleteDialog(dialogTitleText: String, dialogContentText: String, onDismiss: (confirmed: Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss(false) },
        title = { Text(dialogTitleText) },
        text = { Text(dialogContentText) },
        confirmButton = {
            Button(
                onClick = { onDismiss(true) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss(false) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground)
            ) {
                Text("Cancel")
            }
        },
        properties = DialogProperties(dismissOnClickOutside = true),
    )
}

@Composable
fun AcceptDenyDialog(dialogTitleText: String, dialogContentText: String, onDismiss: (confirmed: Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss(false) },
        title = { Text(dialogTitleText) },
        text = { Text(dialogContentText) },
        confirmButton = {
            Button(
                onClick = { onDismiss(true) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Accept")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss(false) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground)
            ) {
                Text("Cancel")
            }
        },
        properties = DialogProperties(dismissOnClickOutside = true),
    )
}