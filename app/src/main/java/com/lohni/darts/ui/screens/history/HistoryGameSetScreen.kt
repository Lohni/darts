package com.lohni.darts.ui.screens.history


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.lohni.darts.R
import com.lohni.darts.common.FormatUtil
import com.lohni.darts.room.dto.ShortSummary
import com.lohni.darts.ui.screens.game.PostGameStatisticRow
import com.lohni.darts.viewmodel.GameSummaryViewModel
import com.lohni.darts.viewmodel.HistoryViewModel
import kotlinx.serialization.Serializable

@Serializable
data class HistoryGameSetRoute(
    val gameId: Int,
    val setId: Int,
    val setOrdinal: Int
)

@Composable
fun HistoryGameSetScreen(navController: NavController) {
    val args = navController.currentBackStackEntry?.toRoute<HistoryGameSetRoute>()

    val gameId = args!!.gameId
    val setId = args.setId
    val setOrdinal = args.setOrdinal

    val historyViewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory)
    historyViewModel.setGameAndSetId(gameId, setId)

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
                        contentDescription = "Back",
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        }
                    )
                }
                val game by remember { historyViewModel.game }
                Column(
                    Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        "${game?.gameId ?: ""} - ${game?.gameMode ?: ""} - ${stringResource(R.string.set)} ${setOrdinal + 1}",
                        fontSize = 18.sp
                    )
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp)
                    ) {
                        HistoryGameSetStatistics(gameId, setId)
                    }
                }
            }
            val sets = remember { historyViewModel.legSummaries }
            LazyColumn(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            ) {
                items(sets) {
                    HistoryGameLegEntry(it) { l ->
                        navController.navigate(
                            HistoryGameLegRoute(
                                gameId,
                                setOrdinal,
                                l.id,
                                l.ordinal
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryGameSetStatistics(gameId: Int, sId: Int) {
    val gameSummaryViewModel: GameSummaryViewModel =
        viewModel(factory = GameSummaryViewModel.Factory)
    gameSummaryViewModel.setGameAndSetId(gameId, sId)

    val game by remember { gameSummaryViewModel.game }
    val playerViews = remember { gameSummaryViewModel.gameSummaryList }
    val avgFormat = FormatUtil.createDecimalFormat("0.0")
    LazyColumn {
        item {
            PostGameStatisticRow(stringResource(R.string.player), "${stringResource(R.string.leg)} (${game.gLegs})", stringResource(R.string.average))
            HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
        }
        items(playerViews) {
            PostGameStatisticRow(
                it.player.pName,
                it.legsWon.toString(),
                avgFormat.format(it.average)
            )
        }
    }
}

@Composable
fun HistoryGameLegEntry(legSummary: ShortSummary, onClick: (ShortSummary) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 16.dp, bottom = 8.dp)
            .height(48.dp)
            .clickable { onClick.invoke(legSummary) }
    ) {
        Column(Modifier.weight(4.0F), verticalArrangement = Arrangement.Center) {
            Text("Leg ${legSummary.ordinal + 1}", fontSize = 18.sp)
        }
        val avgFormat = FormatUtil.createDecimalFormat("0.0")
        Column(Modifier.weight(3.0F), verticalArrangement = Arrangement.Center) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.padding(end = 8.dp),
                    painter = painterResource(R.drawable.round_trophy_24),
                    contentDescription = null
                )
                Column {
                    Text(legSummary.winner.pName, textAlign = TextAlign.End)
                    Text(
                        "${stringResource(R.string.average_short_append)} ${avgFormat.format(legSummary.winnerAvg)}",
                        textAlign = TextAlign.End
                    )
                }
            }
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
