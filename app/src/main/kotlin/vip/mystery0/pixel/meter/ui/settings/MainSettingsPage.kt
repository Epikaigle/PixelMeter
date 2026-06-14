package vip.mystery0.pixel.meter.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import me.zhanghai.compose.preference.Preference
import vip.mystery0.pixel.meter.R

@Composable
fun MainSettingsPage(
    selectedPage: SettingsPage?,
    onPageSelected: (SettingsPage) -> Unit,
) {
    SettingsPage.entries.forEach { page ->
        val interactionSource = remember(page) { MutableInteractionSource() }
        Preference(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = { onPageSelected(page) }
                ),
            title = { Text(stringResource(page.titleRes)) },
            summary = if (selectedPage == page) {
                { Text(stringResource(R.string.settings_selected_page)) }
            } else {
                null
            },
        )
    }
}
