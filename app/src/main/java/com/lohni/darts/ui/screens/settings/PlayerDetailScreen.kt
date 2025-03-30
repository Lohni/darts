package com.lohni.darts.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.lohni.darts.R
import com.lohni.darts.ui.composable.BasicDialog
import com.lohni.darts.ui.screens.history.HistoryEntry
import com.lohni.darts.ui.screens.history.HistoryGameDetailRoute
import com.lohni.darts.viewmodel.PlayerViewModel
import kotlinx.serialization.Serializable

@Serializable
data class PlayerDetailRoute(
    val playerId: Int?
)

@Composable
fun PlayerDetailScreen(navController: NavController) {
    val args = navController.currentBackStackEntry?.toRoute<PlayerDetailRoute>()
    val playerViewModel: PlayerViewModel = viewModel(factory = PlayerViewModel.Factory)

    var edit = true
    if (args?.playerId != null) playerViewModel.setPlayerContext(args.playerId)
    else edit = false

    val player by remember { playerViewModel.player }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .clickable {
                            navController.popBackStack()
                        }
                ) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = null
                    )
                }
                Column(
                    Modifier
                        .padding(start = 16.dp)
                ) {
                    val title =
                        if (!edit) stringResource(R.string.create_new_player) else stringResource(R.string.edit_player)
                    Text(title, fontSize = 18.sp)
                }
                if (edit && player.pId > 0) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .weight(0.5F)
                            .padding(end = 16.dp)
                    ) {
                        var showDialog by remember { mutableStateOf(false) }
                        Icon(
                            modifier = Modifier
                                .clickable {
                                    showDialog = true
                                },
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = null
                        )
                        BasicDialog(
                            showDialog,
                            stringResource(R.string.delete),
                            stringResource(R.string.delete),
                            Icons.Rounded.Delete,
                            onDissmiss = {
                                showDialog = false
                                if (it) {
                                    playerViewModel.deletePlayer()
                                    navController.popBackStack()
                                }
                            }
                        ) {
                            Text("${stringResource(R.string.delete_player)} ${player.pName}?")
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.padding(
                    top = 32.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1.0F),
                    value = player.pName,
                    label = { Text(stringResource(R.string.name)) },
                    onValueChange = {
                        playerViewModel.updatePlayer(player.copy(pName = it))
                    }
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Text(
                    stringResource(R.string.activity),
                    fontSize = 14.sp,
                    modifier = Modifier.alpha(0.8F)
                )

                val lastGames = playerViewModel.playerGameHistory
                LazyColumn(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    items(lastGames) {
                        HistoryEntry(it) { g -> navController.navigate(HistoryGameDetailRoute(g.gameId)) }
                    }
                }
            }

            Row(
                modifier = Modifier.padding(
                    start = 32.dp,
                    end = 32.dp,
                    top = 8.dp,
                    bottom = 32.dp
                ),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.weight(1.0F),
                    onClick = {
                        playerViewModel.saveState()
                        navController.popBackStack()
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}