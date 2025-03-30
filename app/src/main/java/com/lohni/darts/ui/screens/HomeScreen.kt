package com.lohni.darts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lohni.darts.R
import com.lohni.darts.ui.screens.history.HistoryEntry
import com.lohni.darts.ui.screens.history.HistoryGameDetailRoute
import com.lohni.darts.ui.screens.settings.mode.GameModeRoute
import com.lohni.darts.ui.screens.settings.PlayerConfigurationRoute
import com.lohni.darts.viewmodel.HomeViewModel

@Composable
fun HomeScreen(navController: NavController) {
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(stringResource(R.string.quick_settings), fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
            }
            Spacer(Modifier.padding(4.dp))
            HomeScreenQuickLink(stringResource(R.string.manage_players)) { navController.navigate(PlayerConfigurationRoute) }
            HomeScreenQuickLink(stringResource(R.string.manage_game_modes)) { navController.navigate(
                GameModeRoute
            ) }

            Row(
                modifier = Modifier.padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(stringResource(R.string.last_games), fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
            }
            val lastGames = homeViewModel.games.collectAsState(emptyList())
            LazyColumn(
                modifier = Modifier.fillMaxHeight().padding(start = 8.dp)
            ) {
                items(lastGames.value.take(10)) {
                    HistoryEntry(it) { g -> navController.navigate(HistoryGameDetailRoute(g.gameId)) }
                }
            }
        }
    }
}

@Composable
fun HomeScreenQuickLink(text: String, onClick: () -> Unit) {
    Row {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .height(68.dp)
                .padding(8.dp)
                .weight(1.0f)
                .clip(shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp))
                .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                .clickable {
                    onClick.invoke()
                }
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.padding(start = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text, fontSize = 18.sp)
                }
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                        .weight(1.0F),
                    horizontalAlignment = Alignment.End
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowForward,
                        null
                    )
                }
            }
        }
    }
}