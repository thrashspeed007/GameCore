package com.thrashspeed.gamecore.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.thrashspeed.gamecore.data.model.ListEntity
import com.thrashspeed.gamecore.navigation.AppScreens
import com.thrashspeed.gamecore.screens.viewmodels.ListsViewModel

@Composable
fun ListContentScreen(topLevelNavController: NavController, listId: Long, viewModel: ListsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val allLists by viewModel.allLists.observeAsState()
    val listEntityState = remember { mutableStateOf<ListEntity?>(null) }

    LaunchedEffect(allLists) {
        val listEntity = allLists?.find { it.id == listId }
        listEntityState.value = listEntity
    }

    val listEntity = listEntityState.value

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
        Text(text = listEntity?.description ?: "", modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            itemsIndexed(listEntity?.games ?: emptyList()) { index, game ->
                GameListItem(index = index, game = game) { gameClickedId ->
                    topLevelNavController.navigate("${AppScreens.GameDetailsScreen.route}/$gameClickedId")
                }
            }
        }
    }
}