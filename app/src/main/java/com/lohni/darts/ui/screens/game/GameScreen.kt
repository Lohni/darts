package com.lohni.darts.ui.screens.game

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.lohni.darts.R
import com.lohni.darts.common.FormatUtil
import com.lohni.darts.game.GameState
import com.lohni.darts.game.LegState
import com.lohni.darts.game.SetState
import com.lohni.darts.game.State
import com.lohni.darts.room.entities.Game
import com.lohni.darts.room.entities.shortString
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType
import com.lohni.darts.ui.composable.BasicDialog
import com.lohni.darts.ui.view.PlayerView
import com.lohni.darts.viewmodel.GameViewModel
import kotlinx.serialization.Serializable

@Serializable
data class GameRoute(
    val gameMode: Int,
    val numSets: Int,
    val numLegs: Int,
    val playerIds: List<Int>,
    val randomOrder: Boolean
)

val SUGGESTION_SUCCESS_COLOR = Color.hsl(87f, 0.55f, 0.55f)

@Composable
fun GameScreen(navController: NavController) {
    val extras = MutableCreationExtras()
    val args = navController.currentBackStackEntry?.toRoute<GameRoute>()

    var routeInfo: GameRoute? = null
    args?.let {
        routeInfo = it
        extras.apply {
            set(GameViewModel.GAME_KEY, it)
            set(
                ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY,
                LocalContext.current.applicationContext as Application
            )
        }
    }
    val gameViewModel: GameViewModel = viewModel(factory = GameViewModel.Factory, extras = extras)

    val game by remember { gameViewModel.game }
    if (game.gId != 0) {
        navController.navigate(
            PostGameRoute(
                gameViewModel.game.value.gId,
                routeInfo?.randomOrder ?: false
            )
        )
    }

    val currPlayerView by remember { gameViewModel.playerView }
    val gameModeName by remember { gameViewModel.gameModeName }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val stateToDisplay by remember { gameViewModel.stateToDisplay }

        if (stateToDisplay !is GameState) {
            val wasStateDisplayed by remember { gameViewModel.wasStateDisplayed }
            stateToDisplay?.let {
                StateDisplayDialog(it, !wasStateDisplayed) {
                    gameViewModel.wasStateDisplayed.value = true
                }
            }
        }
        KeepScreenOn()
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .weight(0.2f)
                    .padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 2.dp)
            ) {
                Column(
                    modifier = Modifier.weight(0.7f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Row {
                        Text(
                            text = gameModeName,
                            fontSize = 20.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(0.2f)
                        )
                        currPlayerView?.stepOrdinal?.let {
                            Text(
                                text = it,
                                fontSize = 20.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(0.2f)
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(0.25f)
                    ) {
                        val format = FormatUtil.createDecimalFormat("0.#")
                        val isDarkMode by remember { gameViewModel.isDarkMode }
                        val color =
                            if (isDarkMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
                        Text(
                            text = format.format(currPlayerView?.score ?: 0f),
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.weight(0.4f),
                            color = color
                        )
                        val suggestion by remember { gameViewModel.suggestion }

                        val first = currPlayerView?.throwOne?.shortString() ?: "-"
                        val second = currPlayerView?.throwTwo?.shortString() ?: "-"
                        val third = currPlayerView?.throwThree?.shortString() ?: "-"

                        CheckoutText(Modifier.weight(0.2f), first, suggestion.first)
                        CheckoutText(Modifier.weight(0.2f), second, suggestion.second)
                        CheckoutText(Modifier.weight(0.2f), third, suggestion.third)
                    }
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier
                            .weight(0.2f)
                            .padding(bottom = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(0.4f),
                            verticalArrangement = Arrangement.Top
                        ) {
                            val format = FormatUtil.createDecimalFormat("0.0")
                            Text(
                                text = format.format(currPlayerView?.average ?: 0f),
                                fontSize = 24.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .alpha(0.8f)
                            )
                        }
                        Text(
                            text = currPlayerView?.throwOne?.shortString() ?: "-",
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(0.2f)
                        )
                        Text(
                            text = currPlayerView?.throwTwo?.shortString() ?: "-",
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(0.2f)
                        )
                        Text(
                            text = currPlayerView?.throwThree?.shortString() ?: "-",
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(0.2f)
                        )
                    }
                    Column(
                        modifier = Modifier.weight(0.3f)
                    ) {
                        Row(
                            modifier = Modifier.weight(0.5f)
                        ) {
                            Text(
                                currPlayerView?.player?.pName ?: "",
                                maxLines = 1,
                                fontSize = 20.sp,
                                modifier = Modifier.weight(0.3f)
                            )
                        }
                        Row(
                            modifier = Modifier.weight(0.5f)
                        ) {
                            Text(
                                text = setSummary(currPlayerView, game),
                                fontSize = 18.sp,
                                modifier = Modifier.weight(0.2f)
                            )
                            Text(
                                text = legSummary(currPlayerView, game),
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(0.2f)
                            )
                            Text(
                                "Dart ${currPlayerView?.darts ?: 0}",
                                fontSize = 18.sp,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(0.2f)
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(0.3f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    if (currPlayerView?.nextPlayer != null) {
                        Column(
                            modifier = Modifier.weight(0.1f)
                        ) {
                            Text(
                                stringResource(R.string.next_player),
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(0.3f)
                            )
                            val format = FormatUtil.createDecimalFormat("0.#")
                            Text(
                                "${currPlayerView?.nextPlayer?.pName ?: ""} - ${
                                    format.format(
                                        currPlayerView?.nextPlayerPoints ?: -1f
                                    )
                                }",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(0.3f)
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.weight(0.12f)
                    ) {
                        val fieldModifier by remember { gameViewModel.fieldModifier }
                        OtherButton(
                            stringResource(R.string.modifier_double),
                            highlighted = fieldModifier == FieldType.Double,
                            onClick = { gameViewModel.changeFieldModifier(FieldType.Double) },
                            modifier = Modifier.weight(0.5f)
                        )
                        OtherButton(
                            stringResource(R.string.modifier_triple),
                            highlighted = fieldModifier == FieldType.Triple,
                            onClick = { gameViewModel.changeFieldModifier(FieldType.Triple) },
                            modifier = Modifier.weight(0.5f)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(0.12f)
                    ) {
                        FieldButton(
                            Field.ONE,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                        FieldButton(
                            Field.TWO,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                        FieldButton(
                            Field.THREE,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(0.12f)
                    ) {
                        FieldButton(
                            Field.FOUR,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                        FieldButton(
                            Field.FIVE,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                        FieldButton(
                            Field.SIX,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(0.12f)
                    ) {
                        FieldButton(
                            Field.SEVEN,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                        FieldButton(
                            Field.EIGHT,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                        FieldButton(
                            Field.NINE,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(0.12f)
                    ) {
                        FieldButton(
                            Field.TEN,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                        FieldButton(
                            Field.ELEVEN,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                        FieldButton(
                            Field.TWELVE,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(0.12f)
                    ) {
                        FieldButton(
                            Field.THIRTEEN,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                        FieldButton(
                            Field.FOURTEEN,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                        FieldButton(
                            Field.FIFTEEN,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(0.12f)
                    ) {
                        FieldButton(
                            Field.SIXTEEN,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                        FieldButton(
                            Field.SEVENTEEN,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                        FieldButton(
                            Field.EIGHTEEN,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(0.12f)
                    ) {
                        FieldButton(
                            Field.NINETEEN,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                        FieldButton(
                            Field.TWENTY,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                        FieldButton(
                            Field.TWENTYFIVE,
                            Modifier.weight(0.3f),
                            onClick = { gameViewModel.onThrow(it) })
                    }
                    Row(
                        modifier = Modifier.weight(0.12f)
                    ) {
                        OtherButton(
                            stringResource(R.string.button_miss),
                            onClick = { gameViewModel.onThrow(Field.ZERO) },
                            modifier = Modifier.weight(0.5f)
                        )
                        OtherButton(
                            stringResource(R.string.button_undo),
                            onClick = { gameViewModel.undo() },
                            modifier = Modifier.weight(0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OtherButton(
    text: String,
    highlighted: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colorButton =
        if (highlighted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.background
    val colorText =
        if (highlighted) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onBackground
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(2.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(15.dp))
            .background(colorButton)
            .clickable { onClick.invoke() }
    ) {
        Text(
            text,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp),
            color = colorText
        )
    }
}

@Composable
fun FieldButton(field: Field, modifier: Modifier = Modifier, onClick: (Field) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(2.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable { onClick.invoke(field) }
    ) {
        Text(
            field.label,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun KeepScreenOn() {
    val currentView = LocalView.current
    DisposableEffect(Unit) {
        currentView.keepScreenOn = true
        onDispose {
            currentView.keepScreenOn = false
        }
    }
}

@Composable
fun StateDisplayDialog(state: State, show: Boolean, onDissmiss: (Boolean) -> Unit) {
    val title = if (state is SetState) stringResource(R.string.set_finished_dialog)
    else stringResource(R.string.leg_finished_dialog)

    val averageFormat = FormatUtil.createDecimalFormat("0.00")
    val scoreFormat = FormatUtil.createDecimalFormat("0.#")
    BasicDialog(
        show,
        title,
        stringResource(R.string.proceed),
        Icons.Rounded.Notifications,
        cancellable = false,
        onDissmiss = {
            onDissmiss.invoke(false)
        }
    ) {
        if (state is LegState) {
            LazyColumn(
                Modifier.padding(start = 16.dp, end = 16.dp)
            ) {
                item {
                    PostGameStatisticRow(
                        stringResource(R.string.player),
                        stringResource(R.string.score),
                        stringResource(R.string.average)
                    )
                    HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
                }
                item {
                    val it = state.getGameView().currPlayerView
                    PostGameStatisticRow(
                        it.player.pName,
                        scoreFormat.format(it.score),
                        averageFormat.format(it.average)
                    )
                }
                items(state.getGameView().otherPlayerState) {
                    PostGameStatisticRow(
                        it.player.pName,
                        scoreFormat.format(it.score),
                        averageFormat.format(it.average)
                    )
                }
            }
        } else if (state is SetState) {
            LazyColumn {
                item {
                    PostGameStatisticRow(
                        stringResource(R.string.player),
                        stringResource(R.string.legs)
                    )
                    HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
                }
                item {
                    val it = state.getGameView().currPlayerView
                    PostGameStatisticRow(
                        it.player.pName,
                        it.legsWon.toString()
                    )
                }
                items(state.getGameView().otherPlayerState) {
                    PostGameStatisticRow(
                        it.player.pName,
                        it.legsWon.toString()
                    )
                }
            }
        }
    }
}

@Composable
fun CheckoutText(modifier: Modifier, throwString: String, suggestion: String) {
    var color = MaterialTheme.colorScheme.onBackground
    if (throwString != "-" && suggestion != "-") {
        color = if (isSuggestionRequirementMet(
                suggestion,
                throwString
            )
        ) SUGGESTION_SUCCESS_COLOR
        else MaterialTheme.colorScheme.error
    }

    Text(
        suggestion,
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        color = color,
        modifier = modifier.alpha(0.8f)
    )
}

private fun setSummary(playerView: PlayerView?, game: Game): String {
    var setsWon = 0
    if (playerView != null) setsWon = playerView.setsWon
    return "Set $setsWon/${game.gSets}"
}

private fun legSummary(playerView: PlayerView?, game: Game): String {
    var legsWon = 0
    if (playerView != null) legsWon = playerView.legsWon
    return "Leg $legsWon/${game.gLegs}"
}

private fun isSuggestionRequirementMet(suggestion: String, score: String): Boolean {
    val suggestionPrefix = suggestion.take(1).takeIf { it in listOf("D", "T", "A") } ?: ""
    val suggestionNumber = suggestion.drop(suggestionPrefix.length)

    val scorePrefix = score.take(1).takeIf { it in listOf("D", "T", "A") } ?: ""
    val scoreNumber = score.drop(scorePrefix.length)

    val isPrefixMet = if (suggestionPrefix == "A") true else suggestionPrefix == scorePrefix
    val isNumberMet = if (suggestionNumber == "All") true else suggestionNumber == scoreNumber
    return isPrefixMet && isNumberMet
}