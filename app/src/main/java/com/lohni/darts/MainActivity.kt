package com.lohni.darts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lohni.darts.ui.navigation.BottomNavigationItem
import com.lohni.darts.ui.screens.HomeScreen
import com.lohni.darts.ui.screens.PlayScreen
import com.lohni.darts.ui.screens.SettingsScreen
import com.lohni.darts.ui.screens.StatisticsScreen
import com.lohni.darts.ui.screens.game.GameRoute
import com.lohni.darts.ui.screens.game.GameScreen
import com.lohni.darts.ui.screens.game.PostGameRoute
import com.lohni.darts.ui.screens.game.PostGameScreen
import com.lohni.darts.ui.screens.history.HistoryGameDetailRoute
import com.lohni.darts.ui.screens.history.HistoryGameDetailScreen
import com.lohni.darts.ui.screens.history.HistoryGameLegRoute
import com.lohni.darts.ui.screens.history.HistoryGameLegScreen
import com.lohni.darts.ui.screens.history.HistoryGameSetRoute
import com.lohni.darts.ui.screens.history.HistoryGameSetScreen
import com.lohni.darts.ui.screens.history.HistoryRoute
import com.lohni.darts.ui.screens.history.HistoryScreen
import com.lohni.darts.ui.screens.settings.CheckoutSuggestionRoute
import com.lohni.darts.ui.screens.settings.CheckoutSuggestionScreen
import com.lohni.darts.ui.screens.settings.PlayerConfigurationRoute
import com.lohni.darts.ui.screens.settings.PlayerDetailRoute
import com.lohni.darts.ui.screens.settings.PlayerDetailScreen
import com.lohni.darts.ui.screens.settings.PlayerScreen
import com.lohni.darts.ui.screens.settings.mode.GameConfigurationRoute
import com.lohni.darts.ui.screens.settings.mode.GameConfigurationScreen
import com.lohni.darts.ui.screens.settings.mode.GameModeDetailRoute
import com.lohni.darts.ui.screens.settings.mode.GameModeDetailScreen
import com.lohni.darts.ui.screens.settings.mode.GameModeRoute
import com.lohni.darts.ui.screens.settings.mode.GameModeScreen
import com.lohni.darts.ui.theme.DartsTheme
import com.lohni.darts.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val settingsViewModel by viewModels<SettingsViewModel> { SettingsViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val darkTheme = settingsViewModel.getDarkMode().collectAsState("false")
            DartsTheme(darkTheme = darkTheme.value.toBooleanStrict()) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    BottomNavigationBar()
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    var navigationSelectedItem by remember {
        mutableIntStateOf(1)
    }

    var hideBottomBar by remember { mutableStateOf(false) }

    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (!hideBottomBar) {
                NavigationBar {
                    BottomNavigationItem.entries.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = index == navigationSelectedItem,
                            label = {
                                Text(stringResource(item.label))
                            },
                            icon = item.icon,
                            onClick = {
                                navigationSelectedItem = index
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id)
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavigationItem.Home.route,
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            composable(BottomNavigationItem.Settings.route) {
                hideBottomBar = false
                SettingsScreen(navController)
            }
            composable(BottomNavigationItem.Home.route) {
                hideBottomBar = false
                HomeScreen(navController)
            }
            composable(BottomNavigationItem.Play.route) {
                hideBottomBar = false
                PlayScreen(navController)
            }
            composable(BottomNavigationItem.Statistics.route) {
                hideBottomBar = false
                StatisticsScreen(navController)
            }
            composable<GameModeRoute> {
                hideBottomBar = false
                GameModeScreen(navController)
            }
            composable<GameModeDetailRoute> {
                hideBottomBar = false
                GameModeDetailScreen(navController)
            }
            composable<GameConfigurationRoute> {
                hideBottomBar = false
                GameConfigurationScreen(navController)
            }
            composable<PlayerConfigurationRoute> {
                hideBottomBar = false
                PlayerScreen(navController)
            }
            composable<PlayerDetailRoute> {
                hideBottomBar = false
                PlayerDetailScreen(navController)
            }
            composable<GameRoute> {
                hideBottomBar = true
                GameScreen(navController)
            }
            composable<PostGameRoute> {
                hideBottomBar = true
                PostGameScreen(navController)
            }
            composable<HistoryRoute> {
                hideBottomBar = false
                HistoryScreen(navController)
            }
            composable<HistoryGameDetailRoute> {
                hideBottomBar = false
                HistoryGameDetailScreen(navController)
            }
            composable<HistoryGameSetRoute> {
                hideBottomBar = false
                HistoryGameSetScreen(navController)
            }
            composable<HistoryGameLegRoute> {
                hideBottomBar = false
                HistoryGameLegScreen(navController)
            }
            composable<CheckoutSuggestionRoute> {
                hideBottomBar = false
                CheckoutSuggestionScreen(navController)
            }
        }
    }
}
