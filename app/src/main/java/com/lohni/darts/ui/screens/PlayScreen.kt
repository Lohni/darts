package com.lohni.darts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lohni.darts.R
import com.lohni.darts.common.FormatUtil
import com.lohni.darts.ui.composable.CustomDropdown
import com.lohni.darts.ui.composable.OutlinedDropdown
import com.lohni.darts.ui.composable.SwipteToDeleteContainer
import com.lohni.darts.viewmodel.GameSelectionViewModel

@Composable
fun PlayScreen(navController: NavController) {
    val gameSelectionViewModel: GameSelectionViewModel =
        viewModel(navController.currentBackStackEntry!!, factory = GameSelectionViewModel.Factory)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.weight(0.4f)
            ) {
                GameConfiguration(gameSelectionViewModel)
            }
            Row(
                modifier = Modifier.weight(0.6f)
            ) {
                GamePlayerSelection(gameSelectionViewModel)
            }

            Row(
                modifier = Modifier.weight(0.1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 16.dp),
                    onClick = {
                        gameSelectionViewModel.play()?.let {
                            navController.navigate(it)
                        }
                    }
                ) {
                    Text(stringResource(R.string.play))
                }
            }
        }
    }
}

@Composable
fun GameConfiguration(gameSelectionViewModel: GameSelectionViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        top = 16.dp,
                        end = 32.dp,
                        start = 32.dp
                    )
                ) {
                    val gameModes = gameSelectionViewModel.allGameModes.collectAsState(emptyList())
                    val selectedGameMode by remember { gameSelectionViewModel.selectedGameMode }
                    OutlinedDropdown(
                        modifier = Modifier,
                        initialItem = selectedGameMode,
                        itemText = { it.gmName },
                        inputLabel = stringResource(R.string.dropdown_mode),
                        selectOptions = gameModes
                    ) {
                        gameSelectionViewModel.setGameMode(it)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(start = 32.dp, end = 32.dp, bottom = 16.dp)
                ) {
                    val numSets by remember { gameSelectionViewModel.numSets }
                    OutlinedTextField(
                        value = numSets.toString(),
                        readOnly = true,
                        onValueChange = {},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        label = { Text(stringResource(R.string.sets)) },
                        modifier = Modifier
                            .height(60.dp)
                            .weight(0.5f)
                    )
                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp, top = 16.dp, bottom = 8.dp)
                            .height(60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            modifier = Modifier
                                .weight(0.5f)
                                .clickable { gameSelectionViewModel.increaseSets() })
                        Icon(
                            painter = painterResource(id = R.drawable.round_remove_24),
                            contentDescription = null,
                            modifier = Modifier
                                .weight(0.5f)
                                .clickable { gameSelectionViewModel.decreaseSets() })
                    }
                    Spacer(modifier = Modifier.weight(0.4f))
                    val numLegs by remember { gameSelectionViewModel.numLegs }
                    OutlinedTextField(
                        value = numLegs.toString(),
                        readOnly = true,
                        onValueChange = {},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        label = { Text(stringResource(R.string.legs)) },
                        modifier = Modifier
                            .height(60.dp)
                            .weight(0.5f)
                    )
                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp, top = 16.dp, bottom = 8.dp)
                            .height(60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Rounded.Add,
                            contentDescription = null,
                            modifier = Modifier
                                .weight(0.5f)
                                .clickable { gameSelectionViewModel.increaseLegs() })
                        Icon(
                            painter = painterResource(id = R.drawable.round_remove_24),
                            contentDescription = null,
                            modifier = Modifier
                                .weight(0.5f)
                                .clickable { gameSelectionViewModel.decreaseLegs() })
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun GamePlayerSelection(gameSelectionViewModel: GameSelectionViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        end = 32.dp,
                        bottom = 8.dp,
                        start = 32.dp,
                        top = 24.dp
                    )
                ) {
                    Text(
                        text = stringResource(R.string.player),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(0.4f)
                    )
                    Text(
                        text = stringResource(R.string.average_for_mode),
                        fontSize = 18.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(0.6f)
                    )
                }

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    val availablePlayers = remember { gameSelectionViewModel.availablePlayers }
                    val playerList = remember { gameSelectionViewModel.selectedPlayers }
                    LazyColumn(
                        modifier = Modifier.padding(bottom = 16.dp, start = 32.dp, end = 32.dp),
                        flingBehavior = ScrollableDefaults.flingBehavior(),
                    ) {
                        itemsIndexed(
                            items = playerList,
                            key = { _, p -> p.player.pId }) { index, item ->
                            SwipteToDeleteContainer(item = item,
                                onDelete = {
                                    gameSelectionViewModel.removePlayer(index)
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .height(48.dp)
                                        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                                ) {
                                    CustomDropdown(
                                        Modifier
                                            .weight(0.5f)
                                            .padding(start = 4.dp),
                                        item.player,
                                        itemText = { it.pName },
                                        availablePlayers,
                                    ) {
                                        gameSelectionViewModel.changePlayer(index, it)
                                    }
                                    val format = FormatUtil.createDecimalFormat("0.0")
                                    Text(
                                        text = format.format(item.average),
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier
                                            .weight(0.5f)
                                            .padding(end = 4.dp)
                                    )
                                }
                            }
                        }
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .clickable {
                                            gameSelectionViewModel.addPlayer()
                                        }
                                ) {
                                    Icon(
                                        Icons.Rounded.Add,
                                        contentDescription = null,
                                        modifier = Modifier.alpha(0.8f)
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(Modifier.height(1.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(
                            end = 32.dp,
                            start = 32.dp
                        )
                        .height(60.dp)
                ) {
                    val randomPlayerOrderSwitch by remember { gameSelectionViewModel.randomOrder }
                    Text(stringResource(R.string.random_player_order), fontSize = 16.sp)
                    Spacer(modifier = Modifier.weight(0.6f))
                    Switch(
                        checked = randomPlayerOrderSwitch,
                        onCheckedChange = { gameSelectionViewModel.toggleRandomOrder() },
                    )
                }
            }
        }
    }
}
