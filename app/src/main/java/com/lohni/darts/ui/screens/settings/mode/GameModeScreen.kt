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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lohni.darts.R
import com.lohni.darts.room.entities.GameMode
import com.lohni.darts.room.enums.GameModeType
import com.lohni.darts.viewmodel.GameModeViewModel
import kotlinx.serialization.Serializable

@Serializable
object GameModeRoute

@Composable
fun GameModeScreen(navController: NavController) {
    val gameModeViewModel: GameModeViewModel = viewModel(factory = GameModeViewModel.Factory)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                Column(
                    Modifier.padding(start = 16.dp)
                ) {
                    Text(stringResource(R.string.game_modes), fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                Column {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            navController.navigate(GameModeDetailRoute(null))
                        }
                    )
                }
            }

            val gameModes = gameModeViewModel.gameModes.collectAsState(emptyList())
            LazyColumn(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                itemsIndexed(gameModes.value) { i, gameMode ->
                    GameModeItems(gameMode) { navController.navigate(GameModeDetailRoute(gameMode.gmId)) }
                }
            }
        }
    }
}

@Composable
fun GameModeItems(gameMode: GameMode, onCLick: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .height(64.dp)
                .padding(start = 16.dp, end = 16.dp)
                .weight(1.0f)
                .clip(shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp))
                .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                .clickable {
                    onCLick.invoke()
                }
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(gameMode.gmName, fontSize = 18.sp)
                    Text("${stringResource(R.string.type)}: ${gameMode.gmType.label}", fontSize = 14.sp)
                }
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                        .weight(1.0F),
                    horizontalAlignment = Alignment.End
                ) {
                    Icon(
                        Icons.Rounded.Edit,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun GameModeItemsPreview() {
    GameModeItems(
        GameMode(
            gmType = GameModeType.CLASSIC,
            gmName = "Preview mode",
            gmStartScore = 40
        )
    ) { }
}
