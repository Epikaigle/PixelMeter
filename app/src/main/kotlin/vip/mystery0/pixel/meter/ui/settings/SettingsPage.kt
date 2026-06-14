package vip.mystery0.pixel.meter.ui.settings

import androidx.annotation.StringRes
import vip.mystery0.pixel.meter.R

enum class SettingsPage(
    @StringRes val titleRes: Int,
) {
    General(R.string.settings_category_general),
    Notification(R.string.settings_category_notification),
    Overlay(R.string.settings_category_overlay),
    Background(R.string.settings_category_background),
    About(R.string.settings_category_about),
}
