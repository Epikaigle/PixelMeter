package vip.mystery0.pixel.meter.ui.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.SliderPreference
import me.zhanghai.compose.preference.SwitchPreference
import vip.mystery0.pixel.meter.R

@Composable
fun GeneralSettingsSection(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val interval by viewModel.samplingInterval.collectAsState(initial = 1500L)
    val speedUnit by viewModel.speedUnit.collectAsState(initial = 0)
    val minSpeedUnit by viewModel.minSpeedUnit.collectAsState(initial = 0)
    val isAutoStartEnabled by viewModel.isAutoStartServiceEnabled.collectAsState(initial = false)
    val canEnableAutoStart by viewModel.canEnableAutoStart.collectAsState()
    val hasOverlayPermission by viewModel.canOverlay.collectAsState()
    val hasNotificationPermission by viewModel.hasNotificationPermission.collectAsState()

    PreferenceCategory(title = { Text(stringResource(R.string.settings_category_general)) })

    SliderPreference(
        value = 0F,
        onValueChange = { },
        sliderValue = interval.toFloat(),
        onSliderValueChange = { viewModel.setSamplingInterval(it.toLong()) },
        valueRange = 1000f..3000f,
        valueSteps = 19,
        title = { Text(stringResource(R.string.settings_sampling_interval)) },
        summary = { Text(stringResource(R.string.settings_sampling_interval_desc)) },
        valueText = { Text("${interval}ms") }
    )

    val labelAuto = stringResource(R.string.settings_speed_unit_auto)
    val speedUnitValues = listOf(labelAuto, "B/s", "KB/s", "MB/s", "GB/s")
    val speedUnitLabel = when (speedUnit) {
        1 -> "B/s"
        2 -> "KB/s"
        3 -> "MB/s"
        4 -> "GB/s"
        else -> labelAuto
    }
    ListPreference(
        value = speedUnitLabel,
        onValueChange = {
            val unit = when (it) {
                "B/s" -> 1
                "KB/s" -> 2
                "MB/s" -> 3
                "GB/s" -> 4
                else -> 0
            }
            viewModel.setSpeedUnit(unit)
        },
        title = { Text(stringResource(R.string.settings_speed_unit_title)) },
        values = speedUnitValues,
        summary = { Text(stringResource(R.string.settings_speed_unit_desc)) }
    )

    val labelNone = stringResource(R.string.settings_min_speed_unit_none)
    val minSpeedUnitValues = listOf(labelNone, "KB/s", "MB/s", "GB/s")
    val minSpeedUnitLabel = when (minSpeedUnit) {
        1 -> "KB/s"
        2 -> "MB/s"
        3 -> "GB/s"
        else -> labelNone
    }
    ListPreference(
        value = minSpeedUnitLabel,
        onValueChange = {
            val unit = when (it) {
                "KB/s" -> 1
                "MB/s" -> 2
                "GB/s" -> 3
                else -> 0
            }
            viewModel.setMinSpeedUnit(unit)
        },
        title = { Text(stringResource(R.string.settings_min_speed_unit_title)) },
        values = minSpeedUnitValues,
        summary = { Text(stringResource(R.string.settings_min_speed_unit_desc)) },
        enabled = speedUnit == 0
    )

    val autoStartSummary = if (canEnableAutoStart) {
        stringResource(R.string.settings_auto_start_service_desc)
    } else {
        stringResource(R.string.settings_auto_start_disabled_reason)
    }

    SwitchPreference(
        value = isAutoStartEnabled,
        onValueChange = { viewModel.setAutoStartServiceEnabled(it) },
        enabled = canEnableAutoStart,
        title = { Text(stringResource(R.string.settings_auto_start_service_title)) },
        summary = { Text(autoStartSummary) }
    )

    val overlayPermissionSummary = if (hasOverlayPermission) {
        stringResource(R.string.settings_permission_granted)
    } else {
        stringResource(R.string.settings_permission_denied)
    }
    Preference(
        title = { Text(stringResource(R.string.settings_permission_overlay)) },
        summary = { Text(overlayPermissionSummary) },
        onClick = {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = "package:${context.packageName}".toUri()
            context.startActivity(intent)
        }
    )

    val notificationPermissionSummary = if (hasNotificationPermission) {
        stringResource(R.string.settings_permission_granted)
    } else {
        stringResource(R.string.settings_permission_denied)
    }
    Preference(
        title = { Text(stringResource(R.string.settings_permission_notification)) },
        summary = { Text(notificationPermissionSummary) },
        onClick = {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            context.startActivity(intent)
        }
    )
}
