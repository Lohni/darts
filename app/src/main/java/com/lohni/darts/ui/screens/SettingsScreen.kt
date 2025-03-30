package com.lohni.darts.ui.screens

import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lohni.darts.R
import com.lohni.darts.ui.screens.history.HistoryRoute
import com.lohni.darts.ui.screens.settings.CheckoutSuggestionRoute
import com.lohni.darts.ui.screens.settings.PlayerConfigurationRoute
import com.lohni.darts.ui.screens.settings.mode.GameModeRoute
import com.lohni.darts.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(navController: NavController) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, top = 32.dp, end = 16.dp)) {
            Text(
                stringResource(R.string.appearance),
                fontSize = 14.sp,
                modifier = Modifier.alpha(0.8F)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            ) {
                Column(Modifier.weight(1.0F), verticalArrangement = Arrangement.Center) {
                    Text(stringResource(R.string.dark_theme), fontSize = 18.sp)
                }

                val checked = settingsViewModel.getDarkMode().collectAsState("false")
                Column {
                    Switch(
                        checked = checked.value.toBooleanStrict(),
                        onCheckedChange = { settingsViewModel.toggleDarkMode() })
                }
            }
            HorizontalDivider(Modifier.height(1.dp))

            Text(
                stringResource(R.string.game),
                fontSize = 14.sp,
                modifier = Modifier
                    .alpha(0.8F)
                    .padding(top = 16.dp)
            )
            HotLink(stringResource(R.string.game_modes)) { navController.navigate(GameModeRoute) }
            HotLink(stringResource(R.string.players)) {
                navController.navigate(
                    PlayerConfigurationRoute
                )
            }
            HotLink(stringResource(R.string.checkout_suggestion)) {
                navController.navigate(
                    CheckoutSuggestionRoute
                )
            }
            HorizontalDivider(Modifier.height(1.dp))
            HotLink(stringResource(R.string.history)) { navController.navigate(HistoryRoute) }
            Spacer(Modifier.weight(1f))
            Row {
                AppVersionDisplay()
            }
        }
    }
}

@Preview
@Composable
fun HotLink(text: String = "", onClick: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 8.dp, bottom = 4.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(5.dp))
            .clickable { onClick.invoke() }
            .padding(top = 8.dp, bottom = 4.dp)
    ) {
        Column(Modifier.weight(1.0F), verticalArrangement = Arrangement.Center) {
            Text(text, fontSize = 18.sp)
        }
        Column {
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}

@Composable
fun AppVersionDisplay() {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val packageName = context.packageName

    var text: String
    try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val versionName = packageInfo.versionName
        val versionCode =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }

        text = "App Version: $versionName ($versionCode)"
    } catch (e: PackageManager.NameNotFoundException) {
        text = "App Version: Unknown"
    }
    Text(
        text,
        fontSize = 14.sp,
        modifier = Modifier
            .padding(bottom = 4.dp)
            .alpha(0.6F)
    )
}