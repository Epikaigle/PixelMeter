# 设计系统 (Design System)

Pixel Meter 严格遵循 Modern Android Development (MAD) 指南，全面采用 Jetpack Compose 构建 UI。

## 1. 主题与配色 (Theming)

### 1.1 Material Design String

- **风格**: **Material 3 + 动态取色**。
- **动态取色 (Dynamic Color)**:
    - 通过 Compose Material 3 的 `dynamicLightColorScheme` / `dynamicDarkColorScheme` 生成色板。
    - UI 颜色直接映射系统壁纸色调，确保与原生系统（Settings, Quick Settings）视觉一致。
- **主要控件**: 使用 M3 标准组件（`Scaffold`、`TopAppBar`、`Switch`、`Card` 等）。
- **设置页面 (Settings)**:
    - 使用 `me.zhanghai.compose.preference` 构建原生风格的设置列表。
  - 手机设备采用二级导航：主设置页只保留常规、通知栏、悬浮窗、后台、关于 5 个入口，点击后进入对应详情页。
  - 手机二级页切换使用短时长横向 96dp 位移转场，并保留返回到主设置页的反向动画。
  - 平板设备采用双栏布局：左侧固定展示父级设置入口，右侧直接展示当前二级设置项。
  - 主设置页不放置快速开关，快速开关保留在 App 主页面。
  - **分块明确**:
      - **General**: 基础采样间隔与权限状态。
      - **Background**: 包含电池优化、最近任务隐藏等保活相关设置。
      - **Notification/Overlay**: 独立配置块。
    - 使用 `com.github.skydoves:colorpicker-compose` 实现颜色选择器。

## 2. 通知栏动态图标 (Notification Icon)

### 实现方案

1. **默认状态**: 首次启动默认**关闭**，需用户手动开启。
2. **创建 Bitmap**:
    - 小图标始终绘制总网速 (`upload + download`)。
    - 通知内容根据显示模式展示总速、仅上行或仅下行，并支持自定义上下行前缀。
3. **Canvas 绘制**: 使用 `Canvas` 和 `Paint` 将文字绘制在 Bitmap 中央。
    - **字体大小**: 需根据系统状态栏高度动态适配，或提供用户手动调节选项。
   - **颜色**: 当前图标文字使用白色绘制；通知可通过 `NotificationCompat.Builder.setColor()` 设置自定义强调色。
3. **IconCompat (Pixel/Android 12+ 适配)**:
    - 当前实现使用 `IconCompat.createWithBitmap(bitmap)` 作为 `SmallIcon`。
    - 必须在真机验证不同背景（浅色/深色/壁纸取色）下的可见性。
4. **Live Update**:
    - 开启 Live Update 时，通知使用静态 `ic_speed` 小图标，并通过 `setShortCriticalText` 和
      `setRequestPromotedOngoing(true)` 显示实时状态文本。

## 3. 悬浮窗 (Floating Window)

### 3.1 窗口类型

- 使用 `TYPE_APPLICATION_OVERLAY`。
- 必须先申请 `SYSTEM_ALERT_WINDOW` 权限。

### 3.2 Compose in WindowManager

- 使用 `ComposeView` 作为 WindowManager 的 View Root。
  -设置 `LifecycleOwner` 和 `SavedStateRegistryOwner` 以确保 Compose 生命周期正常。

```kotlin
val composeView = ComposeView(context).apply {
    setContent {
        PixelPulseTheme {
            OverlayContent(...)
        }
    }
}
windowManager.addView(composeView, params)
```

### 3.3 交互

- **触摸穿透**: 默认情况下悬浮窗应捕获 Touch 事件以支持拖拽。
- **位置记忆**: 每次拖拽结束 (Drag End)，记录当前 (x, y) 坐标到 DataStore，下次启动时恢复。
- **布局配置**: 支持横排/竖排、竖排对齐、横排间距、锁定拖拽、状态栏区域显示和仅竖屏显示。
