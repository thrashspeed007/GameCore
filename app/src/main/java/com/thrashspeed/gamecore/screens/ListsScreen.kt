package com.thrashspeed.gamecore.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.data.model.ListEntity
import com.thrashspeed.gamecore.screens.viewmodels.ListsViewModel

@Composable
fun ListsScreen(topLevelNavController: NavController, navController: NavController, viewModel: ListsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    ListsBodyContent(viewModel = viewModel)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListsBodyContent(viewModel: ListsViewModel) {
    val lists = viewModel.allLists.observeAsState(initial = emptyList()).value
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        CreateListDialog(viewModel = viewModel, onDismiss = { showDialog = false })
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("CREATE")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Add, contentDescription = "CREATE")
                }
            }
        }
    ) {
        Column (
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Lists",
                modifier = Modifier.padding(8.dp),
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (lists.isEmpty()) {
                Row (
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No lists yet!",
                        color = Color.Gray,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.Castle, contentDescription = "Castle", tint = Color.Gray)
                }
            } else {
                LazyColumn(

                ) {
                    items(items = lists) { list ->
                        ListItem(list = list, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ListItem(list: ListEntity, viewModel: ListsViewModel) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .height(80.dp)
    ) {
        if (showDeleteDialog) {
            DeleteListDialog { confirmed ->
                showDeleteDialog = false
                if (confirmed) {
                    viewModel.deleteList(list)
                }
            }
        }

        if (list.games.isNotEmpty()) {
            AsyncImage(
                model = {
                    // TODO
                    // HACER MIXES DE FOTOS DE LOS JUEGOS DE LA LISTA CON UN BOX O ALGO
                },
                contentDescription = list.name + " cover image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(60.dp)
            )
        } else {
            Image(painter = painterResource(id = R.drawable.game_list_icon), contentDescription = "List placeholder image", modifier = Modifier.width(60.dp))
        }
        Text(
            text = list.name,
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            fontSize = 20.sp
        )
        IconButton(onClick = { showDeleteDialog = true }) {
            Icon(imageVector = Icons.Default.Clear, contentDescription = "Delete list icon", tint = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun CreateListDialog(viewModel: ListsViewModel, onDismiss: () -> Unit) {
    var listName by remember { mutableStateOf("") }
    var listDescription by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Create list") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("List name:")
                TextField(
                    value = listName,
                    onValueChange = { listName = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("List description:")
                TextField(
                    value = listDescription,
                    onValueChange = { listDescription = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.insertList(
                        ListEntity(
                            name = listName,
                            description = listDescription,
                            games = emptyList()
                        )
                    )

                    onDismiss()
                }
            ) {
                Text(text = "OK")
            }
        },
        properties = DialogProperties(dismissOnClickOutside = true),
    )
}

@Composable
fun DeleteListDialog(onDismiss: (confirmed: Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss(false) },
        title = { Text("Delete List") },
        text = { Text("Are you sure you want to delete this list?") },
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