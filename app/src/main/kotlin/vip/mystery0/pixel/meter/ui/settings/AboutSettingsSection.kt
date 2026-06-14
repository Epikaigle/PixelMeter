package vip.mystery0.pixel.meter.ui.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.PreferenceCategory
import vip.mystery0.pixel.meter.BuildConfig
import vip.mystery0.pixel.meter.R

@Composable
fun AboutSettingsSection() {
    val uriHandler = LocalUriHandler.current

    PreferenceCategory(title = { Text(stringResource(R.string.settings_category_about)) })
    Preference(
        title = { Text(stringResource(R.string.settings_app_version)) },
        summary = { Text(BuildConfig.VERSION_NAME) }
    )
    Preference(
        title = { Text(stringResource(R.string.settings_github)) },
        summary = { Text("https://github.com/Pixel-Tailor-CN/PixelMeter") },
        onClick = { uriHandler.openUri("https://github.com/Pixel-Tailor-CN/PixelMeter") }
    )
    Preference(
        title = { Text(stringResource(R.string.settings_pixel_tailor)) },
        summary = { Text(stringResource(R.string.settings_pixel_tailor_desc)) },
        onClick = { uriHandler.openUri("https://pixel.mystery0.app") }
    )
    Preference(
        title = { Text(stringResource(R.string.settings_telegram_channel)) },
        summary = { Text(stringResource(R.string.settings_telegram_channel_desc)) },
        onClick = { uriHandler.openUri("https://t.me/pixel_tailor_cn") }
    )
}
