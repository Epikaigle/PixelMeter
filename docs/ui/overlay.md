# Compose Overlay

## 1. 窗口模型

Overlay 使用：

```text
WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
```

启用前必须获得 `SYSTEM_ALERT_WINDOW` 权限。窗口默认包含：

- `FLAG_NOT_FOCUSABLE`
- `FLAG_LAYOUT_IN_SCREEN`
- `FLAG_NOT_TOUCH_MODAL`

允许进入状态栏区域时增加 `FLAG_LAYOUT_NO_LIMITS`，并设置 Cutout Mode。

## 2. Compose 宿主

OverlayWindow 在 Activity 外创建 `ComposeView`，并实现：

- `LifecycleOwner`
- `ViewModelStoreOwner`
- `SavedStateRegistryOwner`

显示时建立 Lifecycle 和 SavedState，隐藏时移除 View、发送销毁事件并清理 ViewModelStore。

## 3. 展示内容

Overlay 显示上传和下载速度，格式由 `SpeedFormatter` 提供。

支持：

- 自定义上下行前缀与顺序。
- 横排或竖排。
- 竖排数值 Start/Center/End 对齐。
- 横排间距。
- 字号、背景内边距和圆角。
- 自定义背景色与文字色。
- 使用主题默认颜色。
- 完全隐藏背景。

## 4. 拖拽与位置

未锁定时通过 `detectDragGestures` 更新 WindowManager X/Y。拖拽结束后将坐标写入 DataStore，下次显示时恢复。

位置坐标使用 px，并允许负数。状态栏区域开关可能改变有效可拖拽范围，必须真机验证 Cutout 设备。

## 5. 条件隐藏

### 横屏隐藏

开启 Portrait Only 后，设备处于 Landscape 时隐藏。

### 沉浸模式隐藏

通过 WindowInsets 观察状态栏和导航栏可见性。当前台应用隐藏任一系统栏时，可将 Overlay 透明并设为不可触摸。

根 ComposeView 仍保持挂载，以便系统栏恢复时继续收到 Insets 并重新显示。

### 低流量自动隐藏

当总网速连续 3 个采样周期低于 `key_overlay_auto_hide_threshold` 时隐藏；速度恢复后重新显示。阈值为 0 时禁用。

## 6. 主题与颜色

Overlay 使用与 App 相同的 `PixelPulseTheme`。默认颜色模式读取 Material 3 Surface/OnSurface；自定义颜色模式使用用户保存的 ARGB。

## 7. 真机验证

- Overlay 权限授予和撤销。
- 拖拽、锁定和重启后位置恢复。
- 横竖屏切换。
- 状态栏、刘海和打孔区域。
- 全屏视频、游戏等沉浸模式。
- 导航模式切换。
- 低流量隐藏和恢复。
