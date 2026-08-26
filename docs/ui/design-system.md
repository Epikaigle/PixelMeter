# UI 设计系统

## 1. 基础原则

Pixel Meter 使用 Jetpack Compose 和 Material 3，目标是贴近 Pixel 原生系统体验：

- 使用 Material 3 标准组件。
- 支持 Edge-to-Edge。
- 尊重系统深色模式和字体缩放。
- 面向用户与无障碍的文本必须国际化。
- 系统功能受限时明确说明 Android 版本和权限条件。

## 2. 主题

### Dynamic Color

默认使用 Material You Dynamic Color，从系统壁纸色板生成 Light/Dark ColorScheme。

### Fixed Color

用户可切换固定主题色。主题代码根据选定颜色生成可用的 Material 3 色板。

### AMOLED Black

仅在以下条件同时满足时启用：

- 固定主题色模式。
- 系统处于深色模式。
- 用户开启 AMOLED Black。

主要背景和 Surface 使用纯黑，同时保持内容对比度。

## 3. 主界面

主界面展示：

- 总网速与上下行速度。
- 监听服务状态和启动/停止操作。
- 通知与 Overlay 快速开关。
- Cloudflare 测速入口。
- 权限或服务启动错误的修复卡片。

Activity 负责系统 Intent 和权限 Launcher；状态与业务操作由 MainViewModel 提供。

## 4. 设置页

设置内容分为：

- General
- Notification
- Overlay
- Background
- About

手机采用主设置目录和二级页面；宽度达到 840dp 时使用双栏：左侧目录，右侧当前内容。

常规设置中提供重新运行首次设置向导的入口。

## 5. 控件与数值

- 开关使用 `SwitchPreference` 或 Material 3 Switch。
- 枚举设置使用 `ListPreference`。
- 连续范围使用 `SliderPreference`。
- 精确数值使用 `TextFieldPreference`。
- 颜色使用 `colorpicker-compose`。
- px、dp、sp 和网速单位应在 UI 中明确标注。

## 6. 响应式与可访问性

- 长页面使用 LazyColumn 或可滚动 Column。
- 按钮组在窄屏下必须保持可读和可触达。
- IconButton 必须提供本地化 `contentDescription`。
- 使用 Start/End 语义支持 RTL。
- 不依赖颜色作为唯一状态提示。
