package com.harian.raweng_basketbaall_gameschedule.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.harian.raweng_basketbaall_gameschedule.ScheduleViewModel
import com.harian.raweng_basketbaall_gameschedule.model.Game
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val APP_TEAM_ID = "1610612748"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel) {
    val games by viewModel.filteredSchedules.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showMissingDataDialog by remember { mutableStateOf(false) }
    LaunchedEffect(games, teams) {
        if (games.isEmpty() && teams.isEmpty()) showMissingDataDialog = true
    }
    if (showMissingDataDialog) {
        NoDataDialog()
        return
    }

    val flatGames = remember(games) { games.sortedBy { it.gametime }.withIndex().toList() }
    val scrollToIndex = flatGames.firstOrNull { it.value.status == 1 }?.index ?: 0
    LaunchedEffect(scrollToIndex) {
        listState.animateScrollToItem(scrollToIndex)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
            val grouped = games.groupBy {
                ZonedDateTime.parse(it.gametime).format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))
            }
            grouped.forEach { (month, gamesInMonth) ->
                stickyHeader {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.DarkGray)
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = month.uppercase(),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }
                itemsIndexed(gamesInMonth) { _, game ->
                    val homeTeam = teams[game.homeTeam.tid]
                    val awayTeam = teams[game.visitorTeam.tid]

                    val gameTime = ZonedDateTime.parse(game.gametime)
                    val status = when (game.status) {
                        1 -> "UPCOMING"
                        2 -> "LIVE"
                        3 -> "FINAL"
                        else -> "TBD"
                    }

                    val isHome = game.homeTeam.tid == APP_TEAM_ID
                    val scopeLabel = if(isHome) "HOME" else "AWAY"

                    val indicator = when (APP_TEAM_ID) {
                        game.homeTeam.tid -> "vs"
                        game.visitorTeam.tid -> "@"
                        else -> ""
                    }


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color(0xFF1E1E1E), shape = MaterialTheme.shapes.medium)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "$status |",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "$scopeLabel |",    
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Spacer(modifier = Modifier.width(5.dp))

                                Text(
                                    text = gameTime.format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy h:mm a")),
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            ImageRequest.Builder(LocalContext.current)
                                                .data(awayTeam?.logo)
                                                .build()
                                        ),
                                        contentDescription = "Visitor Logo",
                                        modifier = Modifier.size(40.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Text(awayTeam?.ta.orEmpty(), color = Color.White, style = MaterialTheme.typography.bodySmall)
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = indicator,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = game.arenaName,
                                        color = Color.LightGray,
                                        style = MaterialTheme.typography.labelSmall,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            ImageRequest.Builder(LocalContext.current)
                                                .data(homeTeam?.logo)
                                                .build()
                                        ),
                                        contentDescription = "Home Logo",
                                        modifier = Modifier.size(40.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Text(homeTeam?.ta.orEmpty(), color = Color.White, style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if(status == "Live"){
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "BUY TICKETS ON planet.com",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )

                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoDataDialog() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            title = { Text("No Schedule Data") },
            text = { Text("Schedule or team files are missing from assets. Please add them to /assets folder.") }
        )
    }
}
