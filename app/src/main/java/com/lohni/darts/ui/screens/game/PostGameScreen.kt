package com.lohni.darts.ui.screens.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.toRoute
import com.lohni.darts.R
import com.lohni.darts.common.FormatUtil
import com.lohni.darts.ui.navigation.BottomNavigationItem
import com.lohni.darts.viewmodel.GameSummaryViewModel
import kotlinx.serialization.Serializable

@Serializable
data class PostGameRoute(
    val gId: Int,
    val randomOrder: Boolean = false
)

@Composable
fun PostGameScreen(navController: NavController) {
    val gameSummaryViewModel: GameSummaryViewModel =
        viewModel(factory = GameSummaryViewModel.Factory)
    val args = navController.currentBackStackEntry?.toRoute<PostGameRoute>()

    args?.let {
        gameSummaryViewModel.setGameId(it.gId)
        gameSummaryViewModel.randomPlayerOrder = it.randomOrder
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.round_trophy_24),
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        val winner by remember { gameSummaryViewModel.winner }
                        Text(winner.pName, fontSize = 25.sp)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp)
                    ) {
                        HistoryGameStatistics(gameSummaryViewModel)
                    }
                }
            }
            Row(
                modifier = Modifier.padding(start = 32.dp, end = 32.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(end = 8.dp),
                    onClick = {
                        navController.navigate(gameSummaryViewModel.getGameRouteForRepeat()) {
                            popUpTo(navController.graph.findStartDestination().id)
                        }
                    }) {
                    Text(stringResource(R.string.rematch))
                }
                Button(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = 8.dp), onClick = {
                        navController.navigate(BottomNavigationItem.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                        }
                    }) {
                    Text(stringResource(R.string.button_continue))
                }
            }
        }
    }
}

@Composable
fun HistoryGameStatistics(gameSummaryViewModel: GameSummaryViewModel) {
    val game by remember { gameSummaryViewModel.game }
    val playerViews = remember { gameSummaryViewModel.gameSummaryList }
    val avgFormat = FormatUtil.createDecimalFormat("0.0")
    val scoreFormat = FormatUtil.createDecimalFormat("0.#")
    if (game.gLegs > 1) {
        LazyColumn {
            item {
                PostGameStatisticRow(
                    stringResource(R.string.player),
                    stringResource(R.string.sets),
                    stringResource(R.string.legs),
                    stringResource(R.string.average_short)
                )
                HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
            }
            items(playerViews) {
                PostGameStatisticRow(
                    it.player.pName,
                    it.setsWon.toString(),
                    it.legsWon.toString(),
                    avgFormat.format(it.average)
                )
            }
        }
    } else {
        LazyColumn {
            item {
                PostGameStatisticRow(
                    stringResource(R.string.player),
                    stringResource(R.string.score),
                    stringResource(R.string.average_short)
                )
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
}


@Composable
fun PostGameStatisticRow(vararg values: String) {
    PostGameStatisticRow(values.asList())
}

@Composable
fun PostGameStatisticRow(values: List<String>) {
    Row {
        val weight = 1f / values.size
        values.forEachIndexed { idx, value ->
            val textAlign = when (idx) {
                0 -> TextAlign.Start
                values.size - 1 -> TextAlign.End
                else -> TextAlign.Center
            }

            val horizAlign = when (idx) {
                0 -> Alignment.Start
                values.size - 1 -> Alignment.End
                else -> Alignment.CenterHorizontally
            }

            Column(
                horizontalAlignment = horizAlign,
                modifier = Modifier.weight(weight)
            ) {
                Text(value, textAlign = textAlign)
            }
        }
    }
}
