# AGENTS.md

本文件是 Pixel Meter 仓库的统一协作规范。所有开发者、自动化工具与智能体处理本仓库任务时，均应优先遵循本文件。

## 1. 协作规则

- 使用中文回复用户。
- 代码注释、KDoc 与项目文档使用中文；运行日志使用英文。
- Interface、Transport、Foreground Service、Overlay、StateFlow、Repository、Live Update 等专业术语保留英文。
- 需求不明确时先澄清，特别是网络接口过滤、权限、Foreground Service、Overlay 和后台运行相关需求。
- 评估功能方案时必须考虑 Android 后台限制、通知权限、Overlay 权限、Foreground Service 类型和 Google Play 政策。
- 核心功能禁止依赖 Root、Shizuku 或 ADB 常驻权限。
- 不在 Activity 中堆叠业务逻辑；状态与持久化逻辑应位于 ViewModel、Repository 或 DataSource。
- 原则上单个源文件不超过 1000 行。

## 2. 项目定位

Pixel Meter 是面向 Google Pixel 和原生/类原生 Android 设备的实时网速监控应用。

核心能力是使用 `ConnectivityManager.NetworkCallback` 识别 Wi-Fi、Cellular、Ethernet 等物理网络，排除带有
`TRANSPORT_VPN` 的虚拟网络，再通过 `TrafficStats.getRxBytes/getTxBytes` 逐接口读取流量计数，避免 VPN 场景下物理接口和虚拟接口被重复统计。

## 3. 工具链与版本来源

- 单模块 Android 应用：`app/`
- Min SDK：31（Android 12）
- Compile SDK / Target SDK：37
- JVM Target：21
- Kotlin、AGP、Compose BOM、依赖版本和 `app-version` 的唯一事实来源：`gradle/libs.versions.toml`
- 当前主要版本：Kotlin 2.4.10、AGP 9.3.2、Compose BOM 2026.08.00
- 全局 opt-in：`ExperimentalMaterial3Api`
- `versionCode`：Git commit 数量
- `versionName`：`app-version` + 构建类型后缀 + Git 信息
- 当前语言资源：英语、简体中文、葡萄牙语、巴西葡萄牙语、俄语
- 默认资源语言：英语，配置见 `app/src/main/res/resources.properties`

## 4. 构建与验证

```bash
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
./gradlew lint
```

Windows 可使用对应的 `gradlew.bat`。Release 构建需要签名配置。

项目默认不编写单元测试或 Android 测试，除非用户明确要求。代码变更后的最低验证要求：

1. 执行 `:app:assembleDebug`。
2. 执行 `lint`。
3. 涉及网速统计、通知、Overlay 或 Foreground Service 时，在 Pixel 真机验证。
4. 涉及数据源时重点验证 VPN 开启后不会重复统计虚拟网络流量。
5. 涉及翻译资源时确认所有 `values-*` 中的字符串完整，避免 `MissingTranslation`。

## 5. 架构

包根路径：`app/src/main/kotlin/vip/mystery0/pixel/meter/`

项目采用单模块分层 MVVM：

```text
SpeedDataSource
  → 缓存物理 Network 与接口名，读取 TrafficStats
NetworkRepository
  → 计算实时速度，汇总 DataStore 设置为 StateFlow
NetworkMonitorService
  ├→ NotificationHelper：基础通知、Bitmap 动态图标、Live Update
  └→ OverlayWindow：Compose + WindowManager 悬浮窗
MainViewModel / SettingsViewModel
  → 将 Repository 状态提供给 Compose UI
```

### 核心组件

- `data/source/impl/SpeedDataSource.kt`
  - 注册 `NetworkCallback`，缓存通过 Transport 过滤后的物理接口。
  - 排除 `TRANSPORT_VPN`，不依赖 `tun0` 等固定接口名黑名单。
- `data/repository/DataStoreRepository.kt`
  - 封装 Preferences DataStore，名称为 `pixel_pulse_preferences`。
  - 管理显示、主题、服务、通知、Overlay 和 Onboarding 状态。
- `data/repository/NetworkRepository.kt`
  - 轮询数据源并计算上下行速度，向服务和 UI 暴露 `StateFlow`。
- `format/SpeedFormatter.kt`
  - 统一主页、通知、Live Update、Overlay 与设置页的网速格式。
- `service/NetworkMonitorService.kt`
  - Foreground Service，Manifest 声明 `specialUse|dataSync`。
  - Android 14+ 使用 `FOREGROUND_SERVICE_TYPE_SPECIAL_USE`，更低版本使用 `DATA_SYNC`。
  - 息屏 2 分钟后暂停采样，亮屏后恢复。
- `service/NotificationHelper.kt`
  - 构建基础服务通知、Bitmap 动态图标和 Android 16+ Live Update。
- `ui/overlay/OverlayWindow.kt`
  - 通过 `ComposeView` + `WindowManager` 宿主 Compose。
  - 支持拖拽、锁定、位置保存、横竖布局、沉浸模式隐藏和低流量自动隐藏。
- `ui/onboarding/OnboardingScreen.kt`
  - 三步首次设置向导，支持跳过、稍后完成、完成并启动和从设置页重新进入。
- `service/tile/`：Quick Settings Tile，控制通知网速和 Overlay。
- `receiver/BootReceiver.kt`：用户开启自动启动后响应开机广播并启动服务。
- `di/AppModule.kt`：Koin 依赖注册入口。

## 6. UI 与功能规范

- 使用 Jetpack Compose、Material 3 和 Material You。
- 默认采用动态取色；支持固定主题色和深色模式 AMOLED Black。
- 手机设置页采用主目录 + 二级页面；宽屏设备采用双栏布局。
- 首次安装默认不启用通知网速、Live Update 或 Overlay，由用户在向导中选择。
- Android 需要 Foreground Service 基础通知；关闭动态通知网速不代表可以移除基础服务通知。
- 通知动态图标绘制总网速；通知内容支持显示模式、自定义前缀、阈值和颜色。
- Live Update 仅在 Android 16+ 使用。
- Overlay 使用 `TYPE_APPLICATION_OVERLAY`，需要用户授权 `SYSTEM_ALERT_WINDOW`。
- Quick Settings 使用 `TileService`，不在 Tile 中承载复杂业务逻辑。

## 7. Android 兼容性要求

- Android 13+ 才检查 `POST_NOTIFICATIONS` 运行时权限；Android 12/12L 不得错误阻止服务启动。
- Android 14+ Foreground Service 启动必须符合后台启动限制。
- BootReceiver 启动服务时应捕获异常，不得使广播处理崩溃。
- Live Update 依赖 Android 16+ API 和 `POST_PROMOTED_NOTIFICATIONS`。
- 修改 Overlay、系统栏、刘海区域和沉浸模式行为时必须真机验证。
- 修改电池优化或后台保活策略时，需要说明系统限制与潜在 Google Play 风险。

## 8. 国际化

- 默认 `values/strings.xml` 必须使用英语，作为未知语言的回退资源。
- `translatable="false"` 只表示不参与翻译，不表示仅在某个 Locale 显示。
- Locale Config 根据 `resources.properties` 和 `values-*` 目录自动生成。
- 翻译由 Weblate 托管；新增字符串后必须补齐当前全部语言，或明确标记不可翻译。
- 文案、格式参数和 XML 转义必须在所有语言中保持兼容。

详见 `docs/architecture/localization.md`。

## 9. 文档规范

文档索引：`docs/README.md`

- 架构说明：`docs/architecture/`
- UI 与交互说明：`docs/ui/`
- 现有产品截图：`docs/*.png`
- **后续所有需求设计文档和实施计划必须放在 `docs/plans/`。**
- 需求设计文档命名：`docs/plans/YYYY-MM-DD-主题-design.md`
- 实施计划命名：`docs/plans/YYYY-MM-DD-主题-plan.md`
- 同一需求的设计与实施计划使用相同主题名称。
- 已完成且仍具长期参考价值的计划可以保留；纯临时记录应在任务结束时清理。
- 修改架构、DataStore、权限、服务生命周期或用户交互后，应同步更新对应文档。

## 10. 任务完成检查

在向用户报告任务完成前检查：

- 是否新增了可复用组件，是否需要更新架构文档。
- 是否改变了权限、后台行为、DataStore Key 或默认值。
- 是否需要更新 README、隐私政策或 `docs/`。
- 是否新增字符串并补齐所有语言。
- 是否执行 Debug 构建和 Lint。
- 是否需要真机验证 Pixel、VPN、通知、Live Update 或 Overlay 场景。
- `gradle/libs.versions.toml` 是否仍是版本信息的唯一事实来源。

## 11. Release 签名

Release 签名优先从 `local.properties` 读取，缺失时回退到同名环境变量：

- `SIGN_KEY_STORE_FILE`
- `SIGN_KEY_STORE_PASSWORD`
- `SIGN_KEY_ALIAS`
- `SIGN_KEY_PASSWORD`
