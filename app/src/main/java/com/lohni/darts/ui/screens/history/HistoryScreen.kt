package com.lohni.darts.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lohni.darts.R
import com.lohni.darts.room.dto.GameSummaryShort
import com.lohni.darts.room.entities.Player
import com.lohni.darts.viewmodel.HistoryViewModel
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
object HistoryRoute

@Composable
fun HistoryScreen(navController: NavController) {
    val historyViewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        }
                    )
                }
                Column(
                    Modifier.padding(start = 16.dp)
                ) {
                    Text(stringResource(R.string.history), fontSize = 18.sp)
                }
            }

            val games = historyViewModel.games.collectAsState(emptyList())
            LazyColumn {
                items(games.value) {
                    HistoryEntry(it) { g -> navController.navigate(HistoryGameDetailRoute(g.gameId)) }
                }
            }
        }
    }
}

@Composable
fun HistoryEntry(gameSummaryShort: GameSummaryShort, onClick: (GameSummaryShort) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 16.dp, bottom = 8.dp)
            .height(48.dp)
            .clickable { onClick.invoke(gameSummaryShort) }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                gameSummaryShort.date.format(DateTimeFormatter.ofPattern("HH:mm")),
                modifier = Modifier.padding(end = 16.dp),
                fontSize = 14.sp
            )
            Text(
                gameSummaryShort.date.format(DateTimeFormatter.ISO_DATE),
                modifier = Modifier.padding(end = 16.dp),
                fontSize = 14.sp
            )
        }

        Column(Modifier.weight(3.0F), verticalArrangement = Arrangement.Center) {
            Text(gameSummaryShort.gameMode, fontSize = 18.sp, maxLines = 1)
            Text(
                "${stringResource(R.string.winner_info)} ${gameSummaryShort.winner.pName}",
                fontSize = 14.sp,
                maxLines = 1
            )
        }
        Column(Modifier.weight(2.0F), verticalArrangement = Arrangement.Center) {
            Text(
                "${stringResource(R.string.set_leg_info_append)} ${gameSummaryShort.sets}/${gameSummaryShort.legs}",
                textAlign = TextAlign.End
            )
            Text(
                "${stringResource(R.string.player_count_info)} ${gameSummaryShort.playerCount}",
                textAlign = TextAlign.End
            )
        }
        Spacer(modifier = Modifier.weight(0.3f))

        Column {
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
fun HistoryEntryPreview() {
    HistoryEntry(
        GameSummaryShort(
            gameId = 0,
            date = LocalDateTime.now(),
            gameMode = "Test",
            sets = 1,
            legs = 1,
            playerCount = 2,
            winner = Player(pName = "Guest")
        )
    ) { }
}
