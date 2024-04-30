package com.thrashspeed.gamecore.screens

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.thrashspeed.gamecore.data.model.GameItem
import com.thrashspeed.gamecore.screens.viewmodels.ListsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddGameToListScreen(topLevelNavController: NavController, game: GameItem, viewModel: ListsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val lists = viewModel.allLists.observeAsState(initial = emptyList()).value
    var showDialog by remember { mutableStateOf(false) }
    val listsToAddTo = remember { mutableListOf<Long>() }

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
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, end = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { topLevelNavController.popBackStack() },
                    ) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "GoBackIcon")
                    }
                    Text(
                        text = "Choose lists to add to",
                        fontSize = 20.sp
                    )
                }

                Button(onClick = {
                    listsToAddTo.forEach {
                        viewModel.addGameToList(game, it)
                    }
                    topLevelNavController.popBackStack()
                }) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Add to lists done icon")
                        Text(text = "DONE")
                    }
                }
            }
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
                        ListItem(list = list, viewModel = viewModel, addMode = true, topLevelNavController = topLevelNavController) { id, added ->
                            if (added) {
                                listsToAddTo.add(id)
                            } else {
                                listsToAddTo.remove(id)
                            }
                        }
                    }
                }
            }
        }
    }
}
