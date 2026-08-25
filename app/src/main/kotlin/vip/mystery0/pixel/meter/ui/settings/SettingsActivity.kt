package vip.mystery0.pixel.meter.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import vip.mystery0.pixel.meter.R
import vip.mystery0.pixel.meter.ui.theme.PixelPulseTheme

private const val SETTINGS_TWO_PANE_MIN_WIDTH_DP = 840
private const val SETTINGS_PAGE_TRANSITION_DURATION_MS = 250
private const val SETTINGS_PAGE_FADE_DURATION_MS = 50
private const val SETTINGS_PAGE_OPEN_FADE_DELAY_MS = 28
private const val SETTINGS_PAGE_CLOSE_FADE_DELAY_MS = 20
private const val SETTINGS_PAGE_TRANSITION_DISTANCE_DP = 96
private val SettingsSidebarWidth = 280.dp

class SettingsActivity : ComponentActivity() {
    private val viewModel by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appThemeMode by viewModel.appThemeMode.collectAsState()
            val appThemeColor by viewModel.appThemeColor.collectAsState()
            val useAmoledBlack by viewModel.isAppThemeUseAmoledBlack.collectAsState()
            PixelPulseTheme(
                themeMode = appThemeMode,
                themeColor = appThemeColor,
                useAmoledBlack = useAmoledBlack
            ) {
                SettingsScreen()
            }
        }
    }

    @Composable
    fun SettingsScreen() {
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        DisposableEffect(lifecycle) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.refreshOverlaySettings()
                }
            }
            lifecycle.addObserver(observer)
            onDispose {
                lifecycle.removeObserver(observer)
            }
        }

        val twoPaneMinWidthPx = with(LocalDensity.current) {
            SETTINGS_TWO_PANE_MIN_WIDTH_DP.dp.roundToPx()
        }
        val isTwoPane = LocalWindowInfo.current.containerSize.width >= twoPaneMinWidthPx
        var selectedPageName by rememberSaveable { mutableStateOf<String?>(null) }
        val selectedPage = selectedPageName?.let { name ->
            SettingsPage.entries.firstOrNull { it.name == name }
        }
        val effectivePage = if (isTwoPane) {
            selectedPage ?: SettingsPage.General
        } else {
            selectedPage
        }
        val appBarTitleRes = if (!isTwoPane && selectedPage != null) {
            selectedPage.titleRes
        } else {
            R.string.title_settings
        }

        BackHandler(enabled = !isTwoPane && selectedPage != null) {
            selectedPageName = null
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(appBarTitleRes)) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (!isTwoPane && selectedPage != null) {
                                    selectedPageName = null
                                } else {
                                    finish()
                                }
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.content_description_back))
                        }
                    }
                )
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            ProvidePreferenceLocals {
                if (isTwoPane) {
                    SettingsTwoPaneContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        selectedPage = effectivePage ?: SettingsPage.General,
                        onPageSelected = { selectedPageName = it.name },
                        viewModel = viewModel
                    )
                } else {
                    SettingsSinglePaneContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        selectedPage = selectedPage,
                        onPageSelected = { selectedPageName = it.name },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSinglePaneContent(
    selectedPage: SettingsPage?,
    onPageSelected: (SettingsPage) -> Unit,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val transitionDistancePx = with(LocalDensity.current) {
        SETTINGS_PAGE_TRANSITION_DISTANCE_DP.dp.roundToPx()
    }
    AnimatedContent(
        targetState = selectedPage,
        modifier = modifier,
        transitionSpec = {
            val isForward = targetState != null && initialState == null
            val enterOffset = if (isForward) transitionDistancePx else -transitionDistancePx
            val exitOffset = if (isForward) -transitionDistancePx else transitionDistancePx
            val enterFade = if (isForward) {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = SETTINGS_PAGE_FADE_DURATION_MS,
                        delayMillis = SETTINGS_PAGE_OPEN_FADE_DELAY_MS,
                        easing = LinearEasing
                    )
                )
            } else {
                EnterTransition.None
            }
            val exitFade = if (isForward) {
                ExitTransition.None
            } else {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = SETTINGS_PAGE_FADE_DURATION_MS,
                        delayMillis = SETTINGS_PAGE_CLOSE_FADE_DELAY_MS,
                        easing = LinearEasing
                    )
                )
            }
            val enterTransition = slideInHorizontally(
                animationSpec = tween(
                    durationMillis = SETTINGS_PAGE_TRANSITION_DURATION_MS,
                    easing = FastOutSlowInEasing
                ),
                initialOffsetX = { enterOffset }
            ) + enterFade
            val exitTransition = slideOutHorizontally(
                animationSpec = tween(
                    durationMillis = SETTINGS_PAGE_TRANSITION_DURATION_MS,
                    easing = FastOutSlowInEasing
                ),
                targetOffsetX = { exitOffset }
            ) + exitFade

            enterTransition togetherWith exitTransition using SizeTransform(clip = false)
        },
        label = "SettingsPageTransition"
    ) { page ->
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                if (page == null) {
                    MainSettingsPage(
                        selectedPage = null,
                        onPageSelected = onPageSelected
                    )
                } else {
                    SettingsPageContent(
                        page = page,
                        viewModel = viewModel
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}

@Composable
private fun SettingsTwoPaneContent(
    selectedPage: SettingsPage,
    onPageSelected: (SettingsPage) -> Unit,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .width(SettingsSidebarWidth)
                .fillMaxHeight()
        ) {
            item {
                MainSettingsPage(
                    selectedPage = selectedPage,
                    onPageSelected = onPageSelected
                )
            }
            item {
                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }

        VerticalDivider()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            item {
                SettingsPageContent(
                    page = selectedPage,
                    viewModel = viewModel
                )
            }
            item {
                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}

@Composable
private fun SettingsPageContent(
    page: SettingsPage,
    viewModel: SettingsViewModel,
) {
    when (page) {
        SettingsPage.General -> GeneralSettingsSection(viewModel)
        SettingsPage.Notification -> NotificationSettingsSection(viewModel)
        SettingsPage.Overlay -> OverlaySettingsSection(viewModel)
        SettingsPage.Background -> BackgroundSettingsSection(viewModel)
        SettingsPage.About -> AboutSettingsSection()
    }
}
