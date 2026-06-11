# 核心功能需求 (Product Requirements)

| 项目       | 内容                                                     |
|----------|--------------------------------------------------------|
| **产品名称** | PixelMeter                                             |
| **版本**   | v1.9.2（来自 `gradle/libs.versions.toml` 的 `app-version`） |
| **定位**   | 专为 Pixel/原生 Android 设计的精准网速指示器（智能剔除 VPN 流量）            |

## 1. 项目背景与痛点

在开启 VPN（如 V2Ray, Clash, WireGuard 等）的环境下，Android 传统的网速显示 App (基于 `TrafficStats`/
`NetworkStatsManager`) 往往会将物理接口（`wlan0`/`rmnet`）与虚拟接口（`tun0`
）的流量叠加计算。这导致通知栏显示的网速通常是实际速度的 **2 倍**，产生误导。

**PixelMeter** 旨在通过 **单一数据源策略** 解决此痛点，利用 `ConnectivityManager` 智能识别物理接口，直接读取
Kernel 数据剔除虚拟接口流量，还原真实网速。

## 2. 核心特性 (Features)

### 2.1 精准流量统计 (Native)

* **核心机制**: 使用 Android 原生 `TrafficStats` 配合 `ConnectivityManager`。
* **智能过滤**:
  * 自动识别所有物理接口 (Wi-Fi, Cellular, Ethernet)。
  * **自动排除** VPN (`tun0` 等) 虚拟接口，无需用户手动配置黑名单。
  * **计算公式**: `TrueSpeed = Sum(Physical_Interfaces_Rx/Tx)`。

### 2.2 原生体验 (Native Experience)

* **Design**: Material 3 + 动态取色，贴近 Pixel 原生系统体验。
* **Android 14+ 适配**: 针对前台服务类型、通知权限和后台限制进行优化。
* **无广告/轻量级**: 专注于核心功能，极低功耗。

### 2.3 多样的显示方式

* **首次启动**: 默认**不开启**任何显示，由用户自行选择通知栏或悬浮窗。
* **通知栏动态图标**: 实时绘制 Bitmap 更新通知栏小图标，图标显示总网速，通知内容可显示总速、仅上行或仅下行。
* **Live Update**: 可通过 `setShortCriticalText` 与 `setRequestPromotedOngoing` 使用系统实时状态提示。
* **桌面悬浮窗**: 支持独立开关、拖拽、锁定、状态栏区域显示、横竖排布局、仅竖屏显示和自定义样式。

### 2.4 实用工具箱

* **Cloudflare 测速**: 集成 Chrome Custom Tabs (CCT) 快速访问 `speed.cloudflare.com`。

## 3. 技术规格 (Technical Requirements)

* **Min SDK**: 31 (Android 12)
* **Compile SDK / Target SDK**: 37
* **架构**: MVVM + Repository
* **语言**: Kotlin
* **UI**: Jetpack Compose
* **DI**: Koin

## 4. 功能需求详情

| ID      | 模块       | 功能点                | 描述                                                                         | 优先级                                                              |
|:--------|:---------|:-------------------|:---------------------------------------------------------------------------|:-----------------------------------------------------------------|
| **F01** | **核心服务** | 前台服务保活             | 启动 Manifest 声明为 `specialUse                                                | dataSync` 的 Foreground Service，需处理 Android 14+ 适配。支持开机自启、最近任务隐藏。 | P0  |
| **F02** | **数据源**  | 核心数据源              | 调用 `ConnectivityManager` + `TrafficStats` 获取过滤后的物理接口流量。                    | P0                                                               |
| **F03** | **UI**   | 仪表盘首页              | 显示当前网速、服务运行状态。                                                             | P0                                                               |
| **F04** | **UI**   | 通知栏更新              | 按采样间隔绘制 Bitmap 并更新 Notification。支持自定义显示模式（总网速/仅上行/仅下行）、文本前缀、低流量阈值和自定义通知颜色。 | P0                                                               |
| **F05** | **UI**   | 悬浮窗                | 实现 Compose 悬浮窗。支持自定义背景/文字颜色、圆角、字号、前缀、顺序、横竖排、对齐、间距、锁定、位置记忆和仅竖屏显示。           | P1                                                               |
| **F06** | **工具**   | 网络测速               | CCT 呼起 Cloudflare Speed Test。                                              | P2                                                               |
| **F07** | **系统**   | 快速设置磁贴             | 提供 TileService，支持我们在下拉栏快速开关悬浮窗和通知显示。                                       | P1                                                               |
| **F08** | **系统**   | 实时更新 (Live Update) | 适配 Android 16+ Status Bar Chip。                                            | P2                                                               |

## 5. 数据存储 (DataStore)

| Key                                 | 类型      | 默认值          | 说明                                              |
|:------------------------------------|:--------|:-------------|:------------------------------------------------|
| `key_live_update`                   | Boolean | `false`      | 通知 Live Update 开关                               |
| `key_notification_enabled`          | Boolean | `true`       | 通知栏开关                                           |
| `key_overlay_enabled`               | Boolean | `false`      | 悬浮窗开关                                           |
| `key_overlay_locked`                | Boolean | `false`      | 悬浮窗位置锁定开关                                       |
| `key_overlay_show_on_status_bar`    | Boolean | `false`      | 悬浮窗是否允许显示到状态栏区域                                 |
| `key_auto_start_service`            | Boolean | `false`      | 开机自启开关 (需权限)                                    |
| `key_hide_from_recents`             | Boolean | `false`      | 最近任务隐藏开关                                        |
| `key_overlay_x`                     | Int     | `100`        | 悬浮窗 X 坐标                                        |
| `key_overlay_y`                     | Int     | `200`        | 悬浮窗 Y 坐标                                        |
| `key_sampling_interval`             | Long    | `1500`       | 采样间隔 (ms)                                       |
| `key_overlay_bg_color`              | Int     | `0xCC000000` | 悬浮窗背景色                                          |
| `key_overlay_text_color`            | Int     | `0xFFFFFFFF` | 悬浮窗文字色                                          |
| `key_overlay_corner_radius`         | Int     | `8`          | 悬浮窗圆角 (dp)                                      |
| `key_overlay_text_size`             | Float   | `10.0`       | 悬浮窗字号 (sp)                                      |
| `key_overlay_text_up`               | String  | `▲ `         | 上行前缀                                            |
| `key_overlay_text_down`             | String  | `▼ `         | 下行前缀                                            |
| `key_overlay_order_up_first`        | Boolean | `true`       | 优先显示上行                                          |
| `key_overlay_direction`             | Int     | `0`          | 悬浮窗方向 (0: 横排, 1: 竖排)                            |
| `key_overlay_alignment`             | Int     | `0`          | 竖排速度数值对齐方式 (0: Start, 1: Center, 2: End)        |
| `key_overlay_meter_spacing`         | Int     | `8`          | 横排上下行间距 (dp)                                    |
| `key_overlay_portrait_only`         | Boolean | `false`      | 是否仅竖屏显示悬浮窗                                      |
| `key_notification_text_up`          | String  | `▲ `         | 通知栏上行前缀                                         |
| `key_notification_text_down`        | String  | `▼ `         | 通知栏下行前缀                                         |
| `key_notification_order_up_first`   | Boolean | `true`       | 通知栏优先显示上行                                       |
| `key_notification_display_mode`     | Int     | `0`          | 通知栏显示模式 (0:Total, 1:Up, 2:Down)                 |
| `key_notification_text_size`        | Float   | `0.65`       | 通知栏图标数字大小                                       |
| `key_notification_unit_size`        | Float   | `0.35`       | 通知栏图标单位大小                                       |
| `key_notification_threshold`        | Long    | `0`          | 通知栏低流量阈值，0 表示关闭                                 |
| `key_notification_low_traffic_mode` | Int     | `0`          | 低流量模式 (0: 静态图标, 1: 动态图标)                        |
| `key_notification_use_custom_color` | Boolean | `false`      | 通知是否使用自定义颜色                                     |
| `key_notification_color`            | Int     | `0`          | 通知自定义颜色                                         |
| `key_overlay_use_default_colors`    | Boolean | `false`      | 悬浮窗是否使用默认系统颜色                                   |
| `key_speed_unit`                    | Int     | `0`          | 网速单位 (0: 自动, 1: B/s, 2: KB/s, 3: MB/s, 4: GB/s) |
| `key_min_speed_unit`                | Int     | `0`          | 自动模式下的最小显示单位 (0: 无, 1: KB/s, 2: MB/s, 3: GB/s)  |

## 6. 非功能性需求 (NFR)

* **功耗控制**: 实施智能休眠策略。屏幕关闭 2 分钟后自动停止后台网速监听计算，屏幕点亮后毫秒级恢复，确保待机零功耗增加。
* **隐私安全**: 仅在本地处理流量计数，绝不上传任何网络流量数据。
