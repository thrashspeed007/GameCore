package com.thrashspeed.gamecore.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.thrashspeed.gamecore.screens.viewmodels.StatsViewModel
import java.util.Calendar

@Composable
fun StatsScreen(topLevelNavController: NavController, navController: NavController, viewModel: StatsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    StatsScreenBodyContent(
        topLevelNavController = topLevelNavController,
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
fun StatsScreenBodyContent(topLevelNavController: NavController, navController: NavController, viewModel: StatsViewModel) {
    val allGames = viewModel.allGames.observeAsState().value

    if (allGames?.isEmpty() == true) {
        Column (
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "Add games to see stats!", fontSize = 18.sp, modifier = Modifier
                .padding(vertical = 32.dp)
                .fillMaxWidth(), textAlign = TextAlign.Center)
        }
    } else {
        val totalPlaytimeMillis = allGames?.sumOf { it.timePlayed } ?: 0

        val totalHours = totalPlaytimeMillis / (1000 * 60 * 60)
        val totalMinutes = (totalPlaytimeMillis % (1000 * 60 * 60)) / (1000 * 60)

        val calendar = Calendar.getInstance()
        val endTimestamp = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startTimestamp = calendar.timeInMillis

        val gamesLastWeek = allGames?.filter { game ->
            game.firstDayOfPlay in startTimestamp..endTimestamp || game.dayOfCompletion in startTimestamp..endTimestamp
        }

        val totalLastWeekPlaytimeMillis = gamesLastWeek?.sumOf { game ->
            val startTime = maxOf(startTimestamp, game.firstDayOfPlay)
            val endTime = minOf(endTimestamp, game.dayOfCompletion)
            endTime - startTime
        } ?: 0

        val totalLastWeekHours = totalLastWeekPlaytimeMillis / (1000 * 60 * 60)

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.AccessTime, contentDescription = "Time stats icon", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Times", fontSize = 20.sp)
            }

            Column {
                Row (
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column (
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(text = "Total time:")
                        Text(text = "Total hours in last week:")
                    }
                    Column (
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(text = "$totalHours hours and $totalMinutes minutes")
                        Text(text = "$totalLastWeekHours hours")
                    }
                }
            }

            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.BarChart, contentDescription = "Top stats icon", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Stats", fontSize = 20.sp)
            }

            Column (
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row (
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "Most played games:")
                    Column {
                        allGames?.sortedByDescending { it.timePlayed }?.take(5)?.forEachIndexed { index, gameEntity ->
                            Text(text = "${index + 1}. ${gameEntity.name}")
                        }
                    }
                }
                Row (
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "Most played genres:")
                    Column {
                        allGames?.flatMap { it.genres.split(",") }
                            ?.groupingBy { it }
                            ?.eachCount()
                            ?.entries
                            ?.sortedByDescending { it.value }
                            ?.take(5)
                            ?.forEachIndexed { index, entry ->
                                Text(text = "${index + 1}. ${entry.key.trim()}")
                            }
                    }
                }
            }
        }
    }
}