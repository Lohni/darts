package com.lohni.darts.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lohni.darts.R

enum class BottomNavigationItem(
    val route: String,
    val icon: @Composable () -> Unit,
    val label: Int
) {
    Settings("settings", icon = { Icon(Icons.Default.Settings, null) }, R.string.nav_settings),
    Home("home", icon = { Icon(Icons.Default.Home, null) }, R.string.nav_home),
    Play("play", icon = { Icon(painterResource(R.drawable.dart_board_target_mono), null, modifier = androidx.compose.ui.Modifier.size(24.dp)) }, R.string.nav_play),
    Statistics(
        "stats",
        icon = { Icon(painterResource(R.drawable.round_statistic_24), null) },
        R.string.nav_statistics
    )
}