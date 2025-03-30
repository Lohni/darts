package com.lohni.darts.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lohni.darts.R
import com.lohni.darts.common.FormatUtil
import com.lohni.darts.room.entities.CheckoutTable
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType
import com.lohni.darts.ui.composable.CustomDropdown
import com.lohni.darts.ui.composable.OutlinedDropdown
import com.lohni.darts.viewmodel.CheckoutSuggestionViewModel
import kotlinx.serialization.Serializable

@Serializable
object CheckoutSuggestionRoute

@Composable
fun CheckoutSuggestionScreen(navController: NavController) {
    val checkoutSuggestionViewModel: CheckoutSuggestionViewModel =
        viewModel(factory = CheckoutSuggestionViewModel.Factory)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val players = remember { checkoutSuggestionViewModel.availablePlayers }
        val selectedPlayer by remember { checkoutSuggestionViewModel.selectedPlayer }
        var selectedCheckoutType by remember { mutableStateOf(FieldType.Double) }
        Column(modifier = Modifier.padding(start = 8.dp, top = 16.dp, end = 8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Column(
                    modifier = Modifier.clickable {
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
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Text(stringResource(R.string.checkout_suggestion), fontSize = 18.sp)
                }

                var deleteDialog by remember { mutableStateOf(false) }
                Column(horizontalAlignment = Alignment.End) {
                    val isDefault = selectedPlayer.pId == -2
                    if (!isDefault) {
                        Icon(
                            Icons.Rounded.Delete,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    deleteDialog = true
                                },
                        )
                    }
                }
                if (deleteDialog) {
                    CheckoutSuggestionDeleteDialog(selectedCheckoutType, selectedPlayer) {
                        deleteDialog = false
                        if (it) {
                            checkoutSuggestionViewModel.deletePlayerSuggestion(
                                selectedCheckoutType,
                                selectedPlayer
                            )
                            checkoutSuggestionViewModel.changeCheckoutType(selectedCheckoutType)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedDropdown(
                    modifier = Modifier
                        .weight(0.25f)
                        .padding(end = 16.dp),
                    initialItem = selectedCheckoutType,
                    itemText = FieldType::name,
                    inputLabel = stringResource(R.string.checkout_type),
                    selectOptions = FieldType.entries.filter { it != FieldType.ALL }
                ) {
                    checkoutSuggestionViewModel.changeCheckoutType(it)
                    selectedCheckoutType = it
                }
                OutlinedDropdown(
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(end = 16.dp),
                    initialItem = selectedPlayer,
                    itemText = Player::pName,
                    inputLabel = stringResource(R.string.player),
                    selectOptions = players
                ) {
                    checkoutSuggestionViewModel.changePlayer(
                        selectedCheckoutType,
                        selectedPlayer,
                        it
                    )
                }
                var addPlayerDialog by remember { mutableStateOf(false) }
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 8.dp, end = 8.dp)
                        .size(24.dp)
                        .clickable {
                            addPlayerDialog = true
                        }
                )
                if (addPlayerDialog) {
                    CheckoutSuggestionAddPlayerDialog(
                        checkoutSuggestionViewModel.playersWithoutSuggestion,
                        selectedCheckoutType
                    ) {
                        addPlayerDialog = false
                        it?.let {
                            checkoutSuggestionViewModel.addSuggestionForPlayer(
                                selectedCheckoutType,
                                it
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                val checkoutTable = remember { checkoutSuggestionViewModel.suggestions }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(checkoutTable, key = { it.ctScore }) {
                        CheckoutTableEntry(it, checkoutSuggestionViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutTableEntry(
    checkoutTable: CheckoutTable,
    checkoutSuggestionViewModel: CheckoutSuggestionViewModel
) {
    var dialog by remember { mutableStateOf(false) }

    if (dialog) {
        CheckoutSuggestionEditDialog(checkoutTable) {
            dialog = false
            it?.let {
                checkoutSuggestionViewModel.replaceSuggestions(checkoutTable, it)
            }
        }
    }

    Row(
        modifier = Modifier
            .clickable { dialog = true }
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(top = 8.dp, bottom = 8.dp, start = 8.dp, end = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val format = FormatUtil.createDecimalFormat("0")
        Text(
            format.format(checkoutTable.ctScore),
            fontSize = 18.sp,
            modifier = Modifier.weight(0.2f)
        )

        val first = getAsNotation(checkoutTable.ctFieldOne, checkoutTable.ctFieldTypeOne)
        Text(
            first,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.2f)
        )

        val second = getAsNotation(checkoutTable.ctFieldTwo, checkoutTable.ctFieldTypeTwo)
        Text(
            second,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.2f)
        )

        val third = getAsNotation(checkoutTable.ctFieldThree, checkoutTable.ctFieldTypeThree)
        Text(
            third,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.2f)
        )
    }
}

@Composable
fun CheckoutSuggestionEditDialog(
    checkoutSuggestion: CheckoutTable,
    onDismissed: (CheckoutTable?) -> Unit
) {
    Dialog(onDismissRequest = { onDismissed.invoke(null) }) {
        Card {
            Column(
                modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val format = FormatUtil.createDecimalFormat("0")
                        Text(
                        "${stringResource(R.string.score_append)} " + format.format(checkoutSuggestion.ctScore),
                            fontSize = 22.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            "${stringResource(R.string.checkout_append)} ${checkoutSuggestion.ctCheckOutType.label}",
                            fontSize = 16.sp
                        )
                    }
                }

                var fieldOne by remember { mutableStateOf(checkoutSuggestion.ctFieldOne) }
                var fieldTypeOne by remember { mutableStateOf(checkoutSuggestion.ctFieldTypeOne) }
                var fieldTwo by remember { mutableStateOf(checkoutSuggestion.ctFieldTwo) }
                var fieldTypeTwo by remember { mutableStateOf(checkoutSuggestion.ctFieldTypeTwo) }
                var fieldThree by remember { mutableStateOf(checkoutSuggestion.ctFieldThree) }
                var fieldTypeThree by remember { mutableStateOf(checkoutSuggestion.ctFieldTypeThree) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                stringResource(R.string.one_number),
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .weight(0.2f)
                                    .padding(4.dp)
                            )
                            CustomDropdown(
                                modifier = Modifier
                                    .weight(0.3f)
                                    .padding(start = 8.dp, end = 8.dp),
                                initialItem = fieldOne,
                                itemText = Field::label,
                                selectOptions = Field.entries.filter { it != Field.ZERO && it != Field.ALL }
                            ) {
                                if (fieldOne == Field.NONE) fieldTypeOne =
                                    checkoutSuggestion.ctCheckOutType
                                fieldOne = it
                            }
                            CustomDropdown(
                                modifier = Modifier.weight(0.4f),
                                initialItem = fieldTypeOne,
                                itemText = FieldType::label,
                                selectOptions = FieldType.entries.filter { it != FieldType.ALL },
                                enabled = fieldOne != Field.NONE,
                            ) { fieldTypeOne = it }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        ) {
                            Text(
                                stringResource(R.string.two_number),
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .weight(0.2f)
                                    .padding(4.dp)
                            )

                            CustomDropdown(
                                modifier = Modifier
                                    .weight(0.3f)
                                    .padding(start = 8.dp, end = 8.dp),
                                initialItem = fieldTwo,
                                itemText = Field::label,
                                selectOptions = Field.entries.filter { it != Field.ZERO && it != Field.ALL }
                            ) {
                                if (fieldTwo == Field.NONE) fieldTypeTwo =
                                    checkoutSuggestion.ctCheckOutType
                                fieldTwo = it
                            }
                            CustomDropdown(
                                modifier = Modifier.weight(0.4f),
                                initialItem = fieldTypeTwo,
                                itemText = FieldType::label,
                                selectOptions = FieldType.entries.filter { it != FieldType.ALL },
                                enabled = fieldTwo != Field.NONE,
                            ) { fieldTypeTwo = it }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                stringResource(R.string.three_number),
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .weight(0.2f)
                                    .padding(4.dp)
                            )
                            CustomDropdown(
                                modifier = Modifier
                                    .weight(0.3f)
                                    .padding(start = 8.dp, end = 8.dp),
                                initialItem = fieldThree,
                                itemText = Field::label,
                                selectOptions = Field.entries.filter { it != Field.ZERO && it != Field.ALL }
                            ) {
                                if (fieldThree == Field.NONE) fieldTypeThree =
                                    checkoutSuggestion.ctCheckOutType
                                fieldThree = it
                            }
                            CustomDropdown(
                                modifier = Modifier.weight(0.4f),
                                initialItem = fieldTypeThree,
                                itemText = FieldType::label,
                                selectOptions = FieldType.entries.filter { it != FieldType.ALL },
                                enabled = fieldThree != Field.NONE,
                            ) { fieldTypeThree = it }
                        }

                        var hasError by remember { mutableStateOf(false) }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .height(40.dp)
                                .padding(top = 8.dp, bottom = 8.dp)
                        ) {
                            val currScore = calcScore(
                                fieldOne,
                                fieldTypeOne,
                                fieldTwo,
                                fieldTypeTwo,
                                fieldThree,
                                fieldTypeThree
                            )
                            val fieldTypeCheckMessage = checkWrongFieldType(
                                checkoutSuggestion.ctCheckOutType,
                                fieldOne,
                                fieldTypeOne,
                                fieldTwo,
                                fieldTypeTwo,
                                fieldThree,
                                fieldTypeThree
                            )

                            if (currScore != checkoutSuggestion.ctScore) {
                                hasError = true
                                Icon(
                                    Icons.Rounded.Warning,
                                    tint = MaterialTheme.colorScheme.error,
                                    contentDescription = null
                                )
                                Text(
                                    "${stringResource(R.string.error_required_score)} ${checkoutSuggestion.ctScore}",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            } else if (fieldTypeCheckMessage.isNotEmpty()) {
                                hasError = true
                                Icon(
                                    Icons.Rounded.Warning,
                                    tint = MaterialTheme.colorScheme.error,
                                    contentDescription = null
                                )
                                Text(
                                    "${stringResource(R.string.error_invalid_checkout)} $fieldTypeCheckMessage",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            } else {
                                hasError = false
                            }
                        }
                        Row(
                            modifier = Modifier.align(Alignment.End),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { onDismissed.invoke(null) },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                            TextButton(
                                enabled = !hasError,
                                onClick = {
                                    val new = checkoutSuggestion.copy(
                                        ctFieldOne = fieldOne,
                                        ctFieldTypeOne = fieldTypeOne,
                                        ctFieldTwo = fieldTwo,
                                        ctFieldTypeTwo = fieldTypeTwo,
                                        ctFieldThree = fieldThree,
                                        ctFieldTypeThree = fieldTypeThree
                                    )

                                    onDismissed.invoke(new)
                                }
                            ) {
                                Text(stringResource(R.string.save))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutSuggestionAddPlayerDialog(
    players: List<Player>,
    checkoutType: FieldType,
    onDismissed: (Player?) -> Unit
) {
    Dialog(onDismissRequest = { onDismissed.invoke(null) }) {
        Card {
            Column(
                modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(R.string.create),
                            fontSize = 22.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            "${stringResource(R.string.checkout_append)} ${checkoutType.label}",
                            fontSize = 16.sp
                        )
                    }
                }
                var selectedPlayer by remember { mutableStateOf(Player(-2, "")) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp)
                ) {
                    OutlinedDropdown(
                        modifier = Modifier,
                        initialItem = selectedPlayer,
                        itemText = Player::pName,
                        inputLabel = stringResource(R.string.select_player),
                        selectOptions = players
                    ) {
                        selectedPlayer = it
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { onDismissed.invoke(null) },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(
                        enabled = selectedPlayer.pId != -2,
                        onClick = {
                            onDismissed.invoke(selectedPlayer)
                        }
                    ) {
                        Text(stringResource(R.string.create))
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutSuggestionDeleteDialog(
    checkoutType: FieldType,
    player: Player,
    onDismissed: (Boolean) -> Unit
) {
    Dialog(onDismissRequest = { onDismissed.invoke(false) }) {
        Card {
            Column(
                modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(R.string.delete),
                            fontSize = 22.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Column {
                        Text(
                            "${stringResource(R.string.checkout_append)} ${checkoutType.label}",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "${stringResource(R.string.player_append)} ${player.pName}",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { onDismissed.invoke(false) },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            onDismissed.invoke(true)
                        }
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                }
            }
        }
    }
}

private fun getAsNotation(field: Field, fieldType: FieldType): String {
    return if (field.fId > 0) fieldType.shortLabel + field.label else "-"
}

private fun calcScore(
    fieldOne: Field,
    fieldTypeOne: FieldType,
    fieldTwo: Field,
    fieldTypeTwo: FieldType,
    fieldThree: Field,
    fieldTypeThree: FieldType
): Float {
    var score = 0
    if (fieldOne.fId > 0) score += fieldOne.fId * fieldTypeOne.ftId
    if (fieldTwo.fId > 0) score += fieldTwo.fId * fieldTypeTwo.ftId
    if (fieldThree.fId > 0) score += fieldThree.fId * fieldTypeThree.ftId
    return score.toFloat()
}

private fun checkWrongFieldType(
    required: FieldType,
    fieldOne: Field,
    fieldTypeOne: FieldType,
    fieldTwo: Field,
    fieldTypeTwo: FieldType,
    fieldThree: Field,
    fieldTypeThree: FieldType
): String {
    var lastFieldType = FieldType.ALL
    if (fieldOne.fId > 0) lastFieldType = fieldTypeOne
    if (fieldTwo.fId > 0) lastFieldType = fieldTypeTwo
    if (fieldThree.fId > 0) lastFieldType = fieldTypeThree

    if (required == FieldType.Single || required == lastFieldType) return ""

    return lastFieldType.label
}