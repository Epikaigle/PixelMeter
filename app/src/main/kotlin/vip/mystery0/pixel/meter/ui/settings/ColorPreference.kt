package vip.mystery0.pixel.meter.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import me.zhanghai.compose.preference.LocalPreferenceTheme
import me.zhanghai.compose.preference.TwoTargetPreference
import vip.mystery0.pixel.meter.R

@Composable
fun ColorPreference(
    title: String,
    color: Color,
    enabled: Boolean = true,
    onColorSelected: (Color) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    val theme = LocalPreferenceTheme.current

    TwoTargetPreference(
        title = { Text(title) },
        enabled = enabled,
        secondTarget = {
            Box(
                modifier = Modifier
                    .padding(horizontal = theme.horizontalSpacing)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        },
        onClick = { if (enabled) showDialog = true }
    )
    if (showDialog) {
        val controller = rememberColorPickerController()
        var selectedColor by remember { mutableStateOf(color) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.color_picker_title)) },
            text = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    HsvColorPicker(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        controller = controller,
                        initialColor = color,
                        onColorChanged = { envelope ->
                            selectedColor = envelope.color
                        }
                    )
                    AlphaSlider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        controller = controller,
                    )
                    BrightnessSlider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        controller = controller,
                    )
                    AlphaTile(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .clip(MaterialTheme.shapes.medium),
                        controller = controller
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onColorSelected(selectedColor)
                        showDialog = false
                    }
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }
}
