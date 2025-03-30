package com.lohni.darts.ui.screens.settings.mode

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.lohni.darts.R
import com.lohni.darts.room.enums.FieldType
import com.lohni.darts.room.enums.GameModeType
import com.lohni.darts.ui.composable.BasicDialog
import com.lohni.darts.ui.composable.OutlinedDropdown
import com.lohni.darts.viewmodel.GameModeViewModel
import kotlinx.serialization.Serializable

@Serializable
data class GameModeDetailRoute(
    val gameMode: Int?
)

@Composable
fun GameModeDetailScreen(navController: NavController) {
    val gameModeViewModel: GameModeViewModel =
        viewModel(navController.currentBackStackEntry!!, factory = GameModeViewModel.Factory)
    val args = navController.currentBackStackEntry?.toRoute<GameModeDetailRoute>()

    if (args?.gameMode != null && gameModeViewModel.gameMode.value.gmId == 0) gameModeViewModel.setGameModeId(
        args.gameMode
    )

    val gameCount by remember { gameModeViewModel.associatedGames }
    val viewMode = GameModeViewMode.getViewMode(args?.gameMode != null, gameCount)
    gameModeViewModel.editable.value = viewMode != GameModeViewMode.INSPECT

    val gameMode by remember { gameModeViewModel.gameMode }

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
                    val title = when (viewMode) {
                        GameModeViewMode.INSPECT -> stringResource(R.string.inspect_game_mode)
                        GameModeViewMode.EDIT -> stringResource(R.string.edit_game_mode)
                        else -> stringResource(R.string.create_game_mode)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(title, fontSize = 18.sp)
                        if (viewMode == GameModeViewMode.INSPECT) {
                            var showDialog by remember { mutableStateOf(false) }
                            Icon(
                                Icons.Rounded.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        showDialog = true
                                    })
                            BasicDialog(
                                showDialog,
                                stringResource(R.string.immutable),
                                stringResource(R.string.close),
                                icon = Icons.Rounded.Info,
                                onDissmiss = { showDialog = false },
                                cancellable = false
                            ) {
                                Text(stringResource(R.string.game_mode_immutable))
                            }
                        }
                    }
                }
                if (viewMode != GameModeViewMode.CREATE) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .weight(0.5F)
                            .padding(end = 8.dp)
                    ) {
                        var showDuplicateDialog by remember { mutableStateOf(false) }
                        Icon(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable {
                                    showDuplicateDialog = true
                                },
                            painter = painterResource(R.drawable.round_content_copy_24),
                            contentDescription = null
                        )
                        BasicDialog(
                            showDuplicateDialog,
                            stringResource(R.string.duplicate),
                            stringResource(R.string.duplicate),
                            Icons.Rounded.Info,
                            onDissmiss = {
                                showDuplicateDialog = false
                                if (it) {
                                    gameModeViewModel.duplicate()
                                    navController.popBackStack()
                                }
                            }
                        ) {
                            Text(stringResource(R.string.game_mode_duplicate_info, gameMode.gmName))
                        }

                        var showDeleteDialog by remember { mutableStateOf(false) }
                        Icon(
                            modifier = Modifier
                                .clickable {
                                    showDeleteDialog = true
                                },
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = null
                        )
                        BasicDialog(
                            showDeleteDialog,
                            stringResource(R.string.delete),
                            stringResource(R.string.delete),
                            Icons.Rounded.Delete,
                            onDissmiss = {
                                showDeleteDialog = false
                                if (it) {
                                    gameModeViewModel.deleteGameMode()
                                    navController.popBackStack()
                                }
                            }
                        ) {
                            Text(stringResource(R.string.delete_game_mode, gameMode.gmName))
                            if (gameCount > 0) {
                                Text(
                                    stringResource(R.string.delete_game_mode_game_hint, gameCount),
                                    modifier = Modifier.padding(top = 8.dp),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                SingleChoiceSegmentedButtonRow(
                    Modifier.weight(1f),
                ) {
                    GameModeType.entries.forEach {
                        val selected = it.gmtId == gameMode.gmType.gmtId
                        SegmentedButton(
                            selected = selected,
                            shape = SegmentedButtonDefaults.itemShape(
                                it.gmtId,
                                GameModeType.entries.size
                            ),
                            enabled = viewMode != GameModeViewMode.INSPECT,
                            onClick = { gameModeViewModel.setGameMode(gameMode.copy(gmType = it)) }
                        ) {
                            Text(it.name)
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .weight(1.0F)
                    .padding(top = 8.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                if (gameMode.gmType == GameModeType.STEP) {
                    StepMode(navController, gameModeViewModel)
                } else {
                    ClassicMode(gameModeViewModel)
                }
            }

            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 16.dp
                ),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.weight(1.0F),
                    onClick = {
                        gameModeViewModel.saveState()
                        navController.popBackStack()
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

@Composable
fun ClassicMode(gameModeViewModel: GameModeViewModel) {
    val gameMode by remember { gameModeViewModel.gameMode }
    val gameModeConfig by remember { gameModeViewModel.gameModeConfig }
    val editable by remember { gameModeViewModel.editable }

    Column {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1.0F),
                value = gameMode.gmName,
                label = { Text(stringResource(R.string.name)) },
                onValueChange = { gameModeViewModel.setGameMode(gameMode.copy(gmName = it)) }
            )
        }
        Text(
            stringResource(R.string.configure),
            fontSize = 14.sp,
            modifier = Modifier
                .alpha(0.8F)
                .padding(top = 8.dp, start = 16.dp)
        )

        Row(
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = gameMode.gmStartScore.toString(),
                singleLine = true,
                label = { Text(stringResource(R.string.start_score)) },
                onValueChange = {
                    val value = if (it != "") it else "0"
                    gameModeViewModel.setGameMode(gameMode.copy(gmStartScore = value.toInt()))
                },
                modifier = Modifier.weight(0.7F),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = editable,
                trailingIcon = {
                    Icon(
                        Icons.Rounded.Clear,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            gameModeViewModel.setGameMode(gameMode.copy(gmStartScore = 0))
                        })
                }
            )
        }
        Row(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedDropdown(
                modifier = Modifier
                    .weight(1.0F)
                    .padding(end = 8.dp),
                initialItem = gameModeConfig.gmcCheckIn,
                itemText = { it.label },
                inputLabel = stringResource(R.string.check_in),
                selectOptions = FieldType.entries,
                enabled = editable
            ) {
                gameModeViewModel.setGameModeConfig(gameModeConfig.copy(gmcCheckIn = it))
            }

            OutlinedDropdown(
                modifier = Modifier
                    .weight(1.0F)
                    .padding(start = 8.dp),
                initialItem = gameModeConfig.gmcCheckOut,
                itemText = { it.label },
                inputLabel = stringResource(R.string.check_out),
                selectOptions = FieldType.entries,
                enabled = editable
            ) {
                gameModeViewModel.setGameModeConfig(gameModeConfig.copy(gmcCheckOut = it))
            }
        }
    }
}

@Composable
fun StepMode(
    navController: NavController,
    gameModeViewModel: GameModeViewModel,
) {
    val gameMode by remember { gameModeViewModel.gameMode }

    Column {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1.0F),
                value = gameMode.gmName,
                label = { Text(stringResource(R.string.name)) },
                onValueChange = { gameModeViewModel.setGameMode(gameMode.copy(gmName = it)) }
            )
        }
        Text(
            stringResource(R.string.configure),
            fontSize = 14.sp,
            modifier = Modifier
                .alpha(0.8F)
                .padding(top = 10.dp, start = 16.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(48.dp)
                    .padding(start = 16.dp, end = 4.dp)
                    .weight(1.0f)
                    .clip(shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp))
                    .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                    .clickable {
                        navController.navigate(GameConfigurationRoute)
                    }
            ) {
                Text(
                    fontSize = 18.sp,
                    text = stringResource(R.string.game_configuration)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            var showDialog by remember { mutableStateOf(false) }
            Text(
                stringResource(R.string.Summary),
                fontSize = 14.sp,
                modifier = Modifier
                    .alpha(0.8F)
                    .padding(top = 24.dp, start = 16.dp)
            )
            Spacer(Modifier.weight(1f))
            Icon(
                painterResource(R.drawable.round_fullscreen_24),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable {
                        showDialog = true
                    })
            BasicDialog(
                show = showDialog,
                title = stringResource(R.string.Summary),
                successLabel = stringResource(R.string.close),
                cancellable = false,
                icon = Icons.Rounded.Info,
                onDissmiss = { showDialog = false }
            ) {
                GameConfigSummary(gameModeViewModel)
            }
        }

        GameConfigSummary(gameModeViewModel)
    }
}

@Composable
fun GameConfigSummary(gameModeViewModel: GameModeViewModel) {
    val gameModeConfig by remember { gameModeViewModel.gameModeConfig }
    val gameMode by remember { gameModeViewModel.gameMode }
    val gameModeSteps = remember { gameModeViewModel.gameModeSteps }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            .verticalScroll(scrollState)
    ) {
        StepSummaryEntry(stringResource(R.string.start_score), gameMode.gmStartScore.toString())
        StepSummaryEntry(
            stringResource(R.string.win_condition),
            gameModeConfig.gmcStepWinCondition.swcName
        )
        StepSummaryEntry(stringResource(R.string.steps), gameModeSteps.size.toString())
        StepSummaryScore(
            stringResource(R.string.score_success),
            calcSchemaSummary(gameModeViewModel.successCalculation.value)
        )
        StepSummaryScore(
            stringResource(R.string.score_failure),
            calcSchemaSummary(gameModeViewModel.failureCalculation.value)
        )
        HorizontalDivider(
            Modifier
                .padding(top = 4.dp)
                .height(1.dp)
        )
        StepSummaryEntry(
            stringResource(R.string.random_step_order),
            gameModeConfig.gmcRandomStepOrder.toString()
        )
        StepSummaryEntry(
            stringResource(R.string.repeat_on_failure),
            gameModeConfig.gmcRepeatOnFailure.toString()
        )
        StepSummaryEntry(
            stringResource(R.string.proceed_on_success),
            gameModeConfig.gmcImmediateProceedOnSuccess.toString()
        )
    }
}

@Composable
fun StepSummaryEntry(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Text(
            label, fontSize = 16.sp, textAlign = TextAlign.Start, modifier = Modifier
                .weight(0.7F)
                .padding(end = 8.dp)
        )
        Text(
            text = value, fontSize = 16.sp, textAlign = TextAlign.End, modifier = Modifier
                .weight(0.3F)
                .padding(start = 8.dp)
        )
    }
}

@Composable
fun StepSummaryScore(label: String, value: String) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(text = label, fontSize = 16.sp, textAlign = TextAlign.Start)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = value, fontSize = 16.sp, textAlign = TextAlign.End, modifier = Modifier
                    .weight(1F)
                    .padding(start = 16.dp)
            )
        }
    }
}

enum class GameModeViewMode {
    CREATE,
    EDIT,
    INSPECT
    ;

    companion object {
        fun getViewMode(exists: Boolean, gameCount: Int): GameModeViewMode {
            return if (exists && gameCount > 0) return INSPECT
            else if (exists) return EDIT
            else CREATE
        }
    }
}
