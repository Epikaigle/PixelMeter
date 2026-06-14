package vip.mystery0.pixel.meter.ui.settings

import android.content.Intent
import android.provider.Settings
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.SwitchPreference
import vip.mystery0.pixel.meter.R

@Composable
fun BackgroundSettingsSection(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val isIgnoringBatteryOptimizations by viewModel.isIgnoringBatteryOptimizations.collectAsState()
    val isHideFromRecents by viewModel.isHideFromRecents.collectAsState(initial = false)

    PreferenceCategory(title = { Text(stringResource(R.string.settings_category_background)) })

    Preference(
        title = { Text(stringResource(R.string.settings_ignore_battery_optimizations_title)) },
        summary = {
            Text(
                if (isIgnoringBatteryOptimizations) {
                    stringResource(R.string.settings_ignore_battery_optimizations_on)
                } else {
                    stringResource(R.string.settings_ignore_battery_optimizations_off)
                }
            )
        },
        onClick = {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = "package:${context.packageName}".toUri()
            context.startActivity(intent)
        }
    )

    SwitchPreference(
        value = isHideFromRecents,
        onValueChange = { viewModel.setHideFromRecents(it) },
        title = { Text(stringResource(R.string.settings_hide_from_recents_title)) },
        summary = { Text(stringResource(R.string.settings_hide_from_recents_desc)) }
    )

    Preference(
        title = { Text(stringResource(R.string.settings_dont_kill_my_app_title)) },
        summary = { Text(stringResource(R.string.settings_dont_kill_my_app_desc)) },
        onClick = {
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
            customTabsIntent.launchUrl(context, "https://dontkillmyapp.com/".toUri())
        }
    )
}
