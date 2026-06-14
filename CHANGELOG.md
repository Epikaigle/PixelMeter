## 新增

- 悬浮窗新增“沉浸模式下隐藏”，当前台应用隐藏状态栏或导航栏时可自动隐藏悬浮窗。
- 悬浮窗新增低流量自动隐藏阈值，支持滑块和 KB/s 手动输入，连续 3 个采样周期低于阈值后隐藏。
- 悬浮窗新增 X/Y 坐标输入，可精确调整位置并实时同步到已显示窗口。
- 悬浮窗新增“隐藏背景”和“背景内边距”设置，可只显示文字或调整文字与背景边缘的距离。

## 修复

- 修复设置页双栏模式在多窗口/分屏场景下按整屏宽度判断导致布局不准确的问题，改为基于实际容器宽度判断。
- 修复悬浮窗隐藏逻辑在需要监听系统栏变化时直接切换可见性的问题，改为保持 ComposeView 挂载并在隐藏时禁用触摸。

## 优化

- 重构设置页为常规、通知、悬浮窗、后台、关于五个独立页面，手机端使用二级导航和转场动画，大屏使用左右双栏布局。
- 将设置页各配置区拆分为独立 Composable，并复用颜色选择组件，降低 SettingsActivity 复杂度。
- 补充新增设置项的英文、简体中文和葡萄牙语文案，并同步更新设置页设计文档。

## English

### New

- Added a floating window option to hide in immersive mode when the foreground app hides the status or navigation bars.
- Added a low-traffic auto-hide threshold with both slider and KB/s text input; the overlay hides after 3 consecutive low-traffic samples.
- Added X/Y position inputs for precise floating window placement with live updates.
- Added transparent background and background padding controls for the floating window.

### Fixes

- Fixed two-pane settings layout detection in split-screen and multi-window by using the actual window container width.
- Fixed overlay hiding behavior by keeping the ComposeView attached for system bar updates while disabling touch when hidden.

### Improvements

- Reworked Settings into General, Notification, Floating Window, Background, and About pages with phone navigation animations and a large-screen two-pane layout.
- Split settings sections into focused Composables and reused a shared color picker preference to reduce SettingsActivity complexity.
- Added English, Simplified Chinese, and Portuguese strings for the new settings and updated the settings design documentation.
