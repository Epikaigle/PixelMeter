# Preferences DataStore

## 1. 存储位置

Pixel Meter 使用 Preferences DataStore，名称为：

```text
pixel_pulse_preferences
```

`DataStoreRepository` 负责 Key 和原始读写，`NetworkRepository` 将设置同步为 `StateFlow` 并提供业务入口。

## 2. 首次安装与旧用户兼容

`key_onboarding_shown` 不存在时：

- Preferences 为空：视为新安装，展示 Onboarding，通知网速默认关闭。
- Preferences 非空：视为旧用户升级，不自动展示 Onboarding，并保持历史通知默认行为。

新用户跳过向导时写入 `key_onboarding_shown=true`；若 DataStore 仍为空，同时写入 `key_notification_enabled=false`。

## 3. Key 列表

### 引导、服务与全局显示

| Key | 类型 | 默认/兼容值 | 说明 |
|---|---|---|---|
| `key_onboarding_shown` | Boolean | 新用户 false；旧数据 true | 是否已自动展示首次向导 |
| `key_auto_start_service` | Boolean | false | 开机后自动启动监听 |
| `key_hide_from_recents` | Boolean | false | 从最近任务隐藏 |
| `key_sampling_interval` | Long | 1500 | 采样间隔，毫秒 |
| `key_speed_unit` | Int | 0 | 0 Auto；1 B/s；2 KB/s；3 MB/s；4 GB/s |
| `key_min_speed_unit` | Int | 0 | Auto 模式最低单位：0 None；1 KB/s；2 MB/s；3 GB/s |

### App 主题

| Key | 类型 | 默认值 | 说明 |
|---|---|---|---|
| `key_app_theme_mode` | Int | Dynamic | 动态取色或固定主题色 |
| `key_app_theme_color` | Int | `DEFAULT_THEME_COLOR` | 固定主题色 |
| `key_app_theme_use_amoled_black` | Boolean | false | 固定主题深色模式使用纯黑 Surface |

### 通知与 Live Update

| Key | 类型 | 默认/兼容值 | 说明 |
|---|---|---|---|
| `key_live_update` | Boolean | false | Android 16+ Live Update |
| `key_notification_enabled` | Boolean | 新用户 false；旧数据 true | 动态通知网速开关 |
| `key_notification_text_up` | String | `▲ ` | 上行前缀 |
| `key_notification_text_down` | String | `▼ ` | 下行前缀 |
| `key_notification_order_up_first` | Boolean | true | 上行优先显示 |
| `key_notification_display_mode` | Int | 0 | 0 Total；1 Upload；2 Download |
| `key_notification_text_size` | Float | 0.65 | Bitmap 数值字号比例 |
| `key_notification_unit_size` | Float | 0.35 | Bitmap 单位字号比例 |
| `key_notification_threshold` | Long | 0 | 低流量阈值，字节/秒；0 禁用 |
| `key_notification_low_traffic_mode` | Int | 0 | 0 静态图标；1 动态低速值 |
| `key_notification_use_custom_color` | Boolean | false | 是否设置通知强调色 |
| `key_notification_color` | Int | 0 | 通知强调色 |

### Overlay

| Key | 类型 | 默认值 | 说明 |
|---|---|---|---|
| `key_overlay_enabled` | Boolean | false | 悬浮窗开关 |
| `key_overlay_locked` | Boolean | false | 锁定位置 |
| `key_overlay_show_on_status_bar` | Boolean | false | 允许进入状态栏和 Cutout 区域 |
| `key_overlay_x` | Int | 100 | X 坐标，px |
| `key_overlay_y` | Int | 200 | Y 坐标，px |
| `key_overlay_bg_color` | Int | `0xCC000000` | 背景色 |
| `key_overlay_text_color` | Int | `0xFFFFFFFF` | 文字色 |
| `key_overlay_corner_radius` | Int | 8 | 圆角，dp |
| `key_overlay_padding` | Int | 8 | 背景内边距，dp |
| `key_overlay_text_size` | Float | 10 | 字号，sp |
| `key_overlay_text_up` | String | `▲ ` | 上行前缀 |
| `key_overlay_text_down` | String | `▼ ` | 下行前缀 |
| `key_overlay_order_up_first` | Boolean | true | 上行优先显示 |
| `key_overlay_hide_background` | Boolean | false | 背景完全透明 |
| `key_overlay_use_default_colors` | Boolean | false | 使用主题 Surface 颜色 |
| `key_overlay_direction` | Int | 0 | 0 横排；1 竖排 |
| `key_overlay_alignment` | Int | 0 | 0 Start；1 Center；2 End |
| `key_overlay_meter_spacing` | Int | 8 | 横排上下行间距，dp |
| `key_overlay_portrait_only` | Boolean | false | 横屏隐藏 |
| `key_overlay_hide_in_immersive_mode` | Boolean | false | 系统栏隐藏时隐藏 |
| `key_overlay_auto_hide_threshold` | Long | 0 | 持续低流量隐藏阈值，字节/秒 |

## 4. 写入与生效

- 主题和大部分 UI 配置通过 StateFlow 即时生效。
- Overlay 位置在拖拽结束时保存。
- 服务运行期间，通知和 Overlay 从 Repository 读取最新设置。
- Onboarding 完成时一次写入通知、Live Update、Overlay 与完成标记。
- 自动启动只影响 BootReceiver 行为，不会立即启动服务。

## 5. 变更要求

新增或修改 Key 时必须：

1. 在 DataStoreRepository 中定义默认值和读写方法。
2. 在 NetworkRepository 中同步 StateFlow。
3. 评估旧用户缺失 Key 时的兼容行为。
4. 更新本文件。
5. 若 Key 面向用户，更新对应 UI 文档和翻译资源。
