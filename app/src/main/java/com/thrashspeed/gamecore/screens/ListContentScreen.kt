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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.thrashspeed.gamecore.data.model.ListEntity
import com.thrashspeed.gamecore.navigation.AppScreens
import com.thrashspeed.gamecore.screens.viewmodels.ListsViewModel
import com.thrashspeed.gamecore.utils.composables.DeleteDialog

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListContentScreen(topLevelNavController: NavController, listId: Long, viewModel: ListsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val allLists by viewModel.allLists.observeAsState()
    val listEntityState = remember { mutableStateOf<ListEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(allLists) {
        val listEntity = allLists?.find { it.id == listId }
        listEntityState.value = listEntity
    }

    val listEntity = listEntityState.value

    Scaffold {
        Column (
            modifier = Modifier.fillMaxSize()
        ) {
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
                    text = listEntity?.name ?: "",
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = listEntity?.description ?: "", modifier = Modifier.padding(horizontal = 16.dp), maxLines = 5)
            Spacer(modifier = Modifier.height(8.dp))

            if (listEntity?.games?.isEmpty() == true) {
                Row (
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No games yet!",
                        color = Color.Gray,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.Castle, contentDescription = "Castle", tint = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    itemsIndexed(listEntity?.games ?: emptyList()) { index, game ->
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (showDeleteDialog) {
                                DeleteDialog(
                                    dialogTitleText = "Delete game",
                                    dialogContentText = "Are you sure you want to delete the game from the list?"
                                ) { confirmed ->
                                    showDeleteDialog = false
                                    if (confirmed) {
                                        viewModel.deleteGameFromList(game, listEntity!!)
                                    }
                                }
                            }

                            Row (
                                modifier = Modifier.weight(1f)
                            ) {
                                GameListItem(index = index, game = game) { gameClickedId ->
                                    topLevelNavController.navigate("${AppScreens.GameDetailsScreen.route}/$gameClickedId")
                                }
                            }

                            IconButton(onClick = {
                                showDeleteDialog = true
                            }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Delete game icon", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}