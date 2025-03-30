package com.lohni.darts.ui.screens.settings.mode

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lohni.darts.R
import com.lohni.darts.room.entities.GameModeStep
import com.lohni.darts.room.entities.ScoreCalculation
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType
import com.lohni.darts.room.enums.ScoreModifier
import com.lohni.darts.room.enums.ScoreType
import com.lohni.darts.room.enums.StepWinCondition
import com.lohni.darts.ui.composable.CustomDropdown
import com.lohni.darts.ui.composable.OutlinedDropdown
import com.lohni.darts.ui.composable.SwipteToDeleteContainer
import com.lohni.darts.viewmodel.GameModeViewModel
import kotlinx.serialization.Serializable

@Serializable
object GameConfigurationRoute

@Composable
fun GameConfigurationScreen(navController: NavController) {
    val gameModeViewModel: GameModeViewModel = navController.previousBackStackEntry?.let {
        viewModel(it, factory = GameModeViewModel.Factory)
    } ?: viewModel(factory = GameModeViewModel.Factory)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        ) {
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
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null
                    )
                }
                Column(
                    Modifier
                        .padding(start = 16.dp)
                ) {
                    Text(stringResource(R.string.game_configuration), fontSize = 18.sp)
                }
            }

            var selected by remember { mutableIntStateOf(0) }

            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                SingleChoiceSegmentedButtonRow(
                    Modifier.weight(1f),
                ) {
                    SegmentedButton(
                        selected = selected == 0,
                        shape = SegmentedButtonDefaults.itemShape(0, 3),
                        onClick = { selected = 0 }
                    ) {
                        Text(stringResource(R.string.game))
                    }
                    SegmentedButton(
                        selected = selected == 1,
                        shape = SegmentedButtonDefaults.itemShape(1, 3),
                        onClick = { selected = 1 }
                    ) {
                        Text(stringResource(R.string.score))
                    }
                    SegmentedButton(
                        selected = selected == 2,
                        shape = SegmentedButtonDefaults.itemShape(2, 3),
                        onClick = { selected = 2 }
                    ) {
                        Text(stringResource(R.string.steps))
                    }
                }
            }

            when (selected) {
                0 -> {
                    BehaviourTab(gameModeViewModel)
                }
                1 -> {
                    ScoreTab(gameModeViewModel)
                }
                else -> {
                    StepTab(gameModeViewModel)
                }
            }
        }
    }
}

@Composable
fun BehaviourTab(gameModeViewModel: GameModeViewModel) {
    val gameMode by remember { gameModeViewModel.gameMode }
    val gameModeConfig by remember { gameModeViewModel.gameModeConfig }
    val editable by remember { gameModeViewModel.editable }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                stringResource(R.string.score),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .alpha(0.8F)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            OutlinedTextField(
                value = gameMode.gmStartScore.toString(),
                singleLine = true,
                enabled = editable,
                label = { Text(stringResource(R.string.start_score)) },
                onValueChange = {
                    val value = if (it != "") it else "0"
                    gameModeViewModel.setGameMode(gameMode.copy(gmStartScore = value.toInt()))
                },
                modifier = Modifier
                    .weight(1F)
                    .height(60.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    Icon(
                        Icons.Rounded.Clear,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            gameModeViewModel.setGameMode(gameMode.copy(gmStartScore = 0))
                        })
                }
            )

            OutlinedDropdown(
                modifier = Modifier
                    .weight(1F)
                    .padding(start = 8.dp),
                initialItem = gameMode.gmScoreType,
                itemText = { it.label },
                inputLabel = stringResource(R.string.score_type),
                enabled = editable,
                selectOptions = ScoreType.entries
            ) {
                gameModeViewModel.setGameMode(gameMode.copy(gmScoreType = it))
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        ) {
            OutlinedDropdown(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .weight(1F),
                initialItem = gameModeConfig.gmcStepWinCondition,
                itemText = { it.swcName },
                inputLabel = stringResource(R.string.win_condition),
                enabled = editable,
                selectOptions = StepWinCondition.entries
            ) {
                gameModeViewModel.setGameModeConfig(gameModeConfig.copy(gmcStepWinCondition = it))
            }
        }
        HorizontalDivider(
            Modifier
                .height(1.dp)
                .padding(top = 16.dp)
        )
        Spacer(Modifier.padding(top = 16.dp))
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                stringResource(R.string.step_behaviour),
                fontSize = 14.sp,
                modifier = Modifier.alpha(0.8F)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1.0F), verticalArrangement = Arrangement.Center) {
                Text(stringResource(R.string.random_step_order), fontSize = 16.sp)
            }

            Column {
                Switch(
                    checked = gameModeConfig.gmcRandomStepOrder,
                    enabled = editable,
                    onCheckedChange = {
                        gameModeViewModel.setGameModeConfig(
                            gameModeConfig.copy(
                                gmcRandomStepOrder = it
                            )
                        )
                    })
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1.0F), verticalArrangement = Arrangement.Center) {
                Text(stringResource(R.string.repeat_on_failure), fontSize = 16.sp)
            }

            Column {
                Switch(
                    checked = gameModeConfig.gmcRepeatOnFailure,
                    enabled = editable,
                    onCheckedChange = {
                        gameModeViewModel.setGameModeConfig(
                            gameModeConfig.copy(
                                gmcRepeatOnFailure = it
                            )
                        )
                    })
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1.0F), verticalArrangement = Arrangement.Center) {
                Text(stringResource(R.string.proceed_on_success), fontSize = 16.sp)
            }

            Column {
                Switch(
                    checked = gameModeConfig.gmcImmediateProceedOnSuccess,
                    enabled = editable,
                    onCheckedChange = {
                        gameModeViewModel.setGameModeConfig(
                            gameModeConfig.copy(
                                gmcImmediateProceedOnSuccess = it
                            )
                        )
                    })
            }
        }
    }
}

@Composable
fun ScoreTab(gameModeViewModel: GameModeViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        ScoreCalculation(
            stringResource(R.string.on_step_success),
            gameModeViewModel.successCalculation,
            { gameModeViewModel.setSuccessCalculation(it) })
        ScoreCalculation(
            stringResource(R.string.on_step_failure),
            gameModeViewModel.failureCalculation,
            { gameModeViewModel.setFailureCalculation(it) })
    }
}

@Composable
fun StepTab(gameModeViewModel: GameModeViewModel) {
    val steps = remember { gameModeViewModel.gameModeSteps }
    val editable by remember { gameModeViewModel.editable }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = true,
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
    ) {
        items(
            items = steps,
            key = { it.gmsId }
        ) { item ->
            if (editable) {
                SwipteToDeleteContainer(
                    item = item,
                    onDelete = { gameModeViewModel.deleteGameModeStep(it) }
                ) {
                    StepItem(item, editable)
                }
            } else {
                StepItem(item)
            }
        }

        if (editable) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(0.5f)
                            .clickable { gameModeViewModel.createGameModeStep() }
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
}

@Composable
fun StepItem(gameModeStep: GameModeStep, editable: Boolean = false) {
    var fieldValue by remember { mutableStateOf(gameModeStep.gmsField) }
    var fieldTypeValue by remember { mutableStateOf(gameModeStep.gmsFieldType) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable { }
    ) {
        Text(gameModeStep.gmsOrdinal.toString(), modifier = Modifier.padding(start = 16.dp))

        Text(
            stringResource(R.string.field_append),
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(0.2f)
        )
        CustomDropdown(
            modifier = Modifier.weight(0.2f),
            initialItem = fieldValue,
            itemText = { it.label },
            enabled = editable,
            selectOptions = Field.entries.filter { it != Field.NONE && it != Field.ZERO }
        ) {
            gameModeStep.gmsField = it
            fieldValue = it
        }

        Text(
            stringResource(R.string.field_type_append),
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(0.3f)
        )
        CustomDropdown(
            modifier = Modifier
                .weight(0.3f)
                .padding(end = 8.dp),
            initialItem = fieldTypeValue,
            itemText = { it.label },
            enabled = editable,
            selectOptions = FieldType.entries
        ) {
            gameModeStep.gmsFieldType = it
            fieldTypeValue = it
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreCalculation(
    title: String,
    calculation: MutableState<ScoreCalculation>,
    onCalcChange: (ScoreCalculation) -> Unit
) {
    val calcSchema by remember { calculation }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            modifier = Modifier
                .padding(top = 16.dp)
                .alpha(0.8F)
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        var typeModifierExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = typeModifierExpanded,
            onExpandedChange = { typeModifierExpanded = it },
            modifier = Modifier
                .weight(0.2F)
                .padding(top = 8.dp)
                .clickable {
                    typeModifierExpanded = true
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .height(50.dp)
                    .clip(shape = RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .menuAnchor(MenuAnchorType.PrimaryEditable),
            ) {
                Text(
                    text = calcSchema.scByTypeModifier.label,
                    modifier = Modifier
                        .weight(1F)
                        .padding(start = 4.dp),
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
            ExposedDropdownMenu(
                expanded = typeModifierExpanded,
                onDismissRequest = { typeModifierExpanded = false }
            ) {
                ScoreModifier.entries.forEach {
                    DropdownMenuItem(
                        text = { Text(it.label) },
                        onClick = {
                            typeModifierExpanded = false
                            onCalcChange.invoke(calcSchema.copy(scByTypeModifier = it))
                        }
                    )
                }
            }
        }

        var typeExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = it },
            modifier = Modifier
                .weight(0.4F)
                .padding(start = 8.dp)
        ) {
            OutlinedTextField(
                value = calcSchema.scByType.label,
                singleLine = true,
                readOnly = true,
                enabled = calcSchema.scByTypeModifier != ScoreModifier.NONE,
                label = { Text(stringResource(R.string.type)) },
                onValueChange = {},
                modifier = Modifier
                    .weight(1F)
                    .height(60.dp)
                    .menuAnchor(MenuAnchorType.PrimaryEditable),
                trailingIcon = {
                    Icon(
                        Icons.Rounded.ArrowDropDown,
                        contentDescription = null
                    )
                }
            )
            ExposedDropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false }
            ) {
                ScoreType.entries.forEach {
                    DropdownMenuItem(
                        text = { Text(it.label) },
                        onClick = {
                            typeExpanded = false
                            onCalcChange.invoke(calcSchema.copy(scByType = it))
                        }
                    )
                }
            }
        }

        var valueModifierExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = valueModifierExpanded,
            onExpandedChange = { valueModifierExpanded = it },
            modifier = Modifier
                .weight(0.2F)
                .padding(start = 8.dp, top = 8.dp)
                .clickable { valueModifierExpanded = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .height(50.dp)
                    .clip(shape = RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .menuAnchor(MenuAnchorType.PrimaryEditable),
            ) {
                Text(
                    text = calcSchema.scByValueModifier.label,
                    modifier = Modifier
                        .weight(1F)
                        .padding(start = 4.dp),
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
            ExposedDropdownMenu(
                expanded = valueModifierExpanded,
                onDismissRequest = { valueModifierExpanded = false }
            ) {
                ScoreModifier.entries.forEach {
                    DropdownMenuItem(
                        text = { Text(it.label) },
                        onClick = {
                            valueModifierExpanded = false
                            onCalcChange.invoke(calcSchema.copy(scByValueModifier = it))
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = calcSchema.scByValue.toString(),
            singleLine = true,
            label = { Text(stringResource(R.string.value)) },
            enabled = calcSchema.scByValueModifier != ScoreModifier.NONE,
            onValueChange = {
                val changedValue = if (it != "") it else "0"
                onCalcChange.invoke(calcSchema.copy(scByValue = changedValue.toInt()))
            },
            modifier = Modifier
                .weight(0.3F)
                .height(60.dp)
                .padding(start = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                Icon(
                    Icons.Rounded.Clear,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        onCalcChange.invoke(calcSchema.copy(scByValue = 0))
                    })
            }
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Text(
            text = calcSchemaSummary(calcSchema),
            fontSize = 14.sp,
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp)
                .alpha(0.8F)
        )
    }
}

fun calcSchemaSummary(calcSchema: ScoreCalculation): String {
    val text = StringBuilder("Score = Score")
    if (calcSchema.scByTypeModifier != ScoreModifier.NONE) {
        var spacing = " "
        if (calcSchema.scByTypeModifier == ScoreModifier.MULTIPLY || calcSchema.scByValueModifier == ScoreModifier.DIVIDE) {
            spacing = ""
        }
        text.append("$spacing${calcSchema.scByTypeModifier.label}$spacing${calcSchema.scByType.label}")
    }
    if (calcSchema.scByValueModifier != ScoreModifier.NONE) {
        var spacing = " "
        if (calcSchema.scByValueModifier == ScoreModifier.MULTIPLY || calcSchema.scByValueModifier == ScoreModifier.DIVIDE) {
            spacing = ""
        }
        text.append("$spacing${calcSchema.scByValueModifier.label}$spacing${calcSchema.scByValue}")
    }
    return text.toString()
}
