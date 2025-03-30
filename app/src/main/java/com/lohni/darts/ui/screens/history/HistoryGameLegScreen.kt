package com.lohni.darts.ui.screens.history


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.lohni.darts.R
import com.lohni.darts.common.FormatUtil
import com.lohni.darts.room.entities.shortString
import com.lohni.darts.room.enums.GameModeType
import com.lohni.darts.ui.screens.game.PostGameStatisticRow
import com.lohni.darts.ui.view.TurnView
import com.lohni.darts.viewmodel.GameSummaryViewModel
import com.lohni.darts.viewmodel.HistoryViewModel
import kotlinx.serialization.Serializable

@Serializable
data class HistoryGameLegRoute(
    val gameId: Int,
    val setOrdinal: Int,
    val legId: Int,
    val legOrdinal: Int
)

@Composable
fun HistoryGameLegScreen(navController: NavController) {
    val args = navController.currentBackStackEntry?.toRoute<HistoryGameLegRoute>()

    val gameId = args!!.gameId
    val setOrdinal = args.setOrdinal
    val legId = args.legId
    val legOrdinal = args.legOrdinal

    val historyViewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory)
    historyViewModel.setGameAndLegId(gameId, legId)

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
                        contentDescription = null,
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
                        "${game?.gameId ?: ""} - ${game?.gameMode ?: ""} - ${stringResource(R.string.set)} ${setOrdinal + 1} - ${
                            stringResource(
                                R.string.leg
                            )
                        } ${legOrdinal + 1}",
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
                        HistoryGameLegStatistics(gameId, legId)
                    }
                }
            }

            val throwViews = remember { historyViewModel.turnSummaries }
            LazyColumn(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            ) {

                itemsIndexed(throwViews) { i, view ->
                    var background = MaterialTheme.colorScheme.background
                    val isBusted = view.throws.any { it.tBust }
                    if (isBusted) {
                        background = MaterialTheme.colorScheme.errorContainer
                    } else if (i == throwViews.size - 1) {
                        background = MaterialTheme.colorScheme.tertiaryContainer
                    }

                    HistoryGameTurnEntry(background, view)
                }
            }
        }
    }
}

@Composable
fun HistoryGameLegStatistics(gId: Int, lId: Int) {
    val gameSummaryViewModel: GameSummaryViewModel =
        viewModel(factory = GameSummaryViewModel.Factory)
    gameSummaryViewModel.setGameAndLegId(gId, lId)

    val playerViews = remember { gameSummaryViewModel.gameSummaryList }
    val avgFormat = FormatUtil.createDecimalFormat("0.0")
    val scoreFormat = FormatUtil.createDecimalFormat("0.#")
    LazyColumn {
        item {
            PostGameStatisticRow(stringResource(R.string.player), stringResource(R.string.score), stringResource(R.string.average))
            HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
        }
        items(playerViews) {
            PostGameStatisticRow(
                it.player.pName,
                scoreFormat.format(it.score),
                avgFormat.format(it.average)
            )
        }
    }
}

@Composable
fun HistoryGameTurnEntry(background: Color, turnSummary: TurnView) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 16.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
    ) {
        Column(
            Modifier
                .weight(0.4f)
                .padding(start = 8.dp, top = 4.dp, bottom = 4.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(turnSummary.player.pName, fontSize = 18.sp)
        }
        Column(Modifier.weight(0.3f), verticalArrangement = Arrangement.Center) {
            val scoreFormat = FormatUtil.createDecimalFormat("0.#")
            var currScore = turnSummary.startScore
            turnSummary.throws.forEach {
                if (turnSummary.gameModeType == GameModeType.CLASSIC) {
                    currScore -= it.tScore
                } else {
                    currScore += it.tScore
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = it.shortString(),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.2f)
                    )
                    Text(
                        text = scoreFormat.format(currScore),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.2f)
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .weight(0.3f)
                .padding(end = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(text = (turnSummary.turn + 1).toString(), fontSize = 18.sp)
        }
    }
}
