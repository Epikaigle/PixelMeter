# 总体架构

## 1. 项目形态

Pixel Meter 是单模块 Android 应用，主模块为 `app/`，包根路径为 `vip.mystery0.pixel.meter`。

- 语言：Kotlin
- UI：Jetpack Compose + Material 3
- 架构：分层 MVVM + Repository
- 依赖注入：Koin
- 持久化：Preferences DataStore
- 状态：StateFlow
- 最低版本：Android 12 / API 31

依赖和工具链版本以 `gradle/libs.versions.toml` 为准。

## 2. 分层

```text
Android System APIs
  ├─ ConnectivityManager / NetworkCallback
  ├─ TrafficStats
  ├─ NotificationManager
  ├─ WindowManager
  └─ DataStore
          ↓
DataSource / Repository
  ├─ SpeedDataSource
  ├─ DataStoreRepository
  └─ NetworkRepository
          ↓
Service / ViewModel
  ├─ NetworkMonitorService
  ├─ MainViewModel
  └─ SettingsViewModel
          ↓
Compose UI
  ├─ MainActivity
  ├─ SettingsActivity
  ├─ OnboardingScreen
  └─ OverlayWindow
```

## 3. 实时网速数据流

1. `SpeedDataSource` 通过 `NetworkCallback` 缓存物理网络与接口名称。
2. `NetworkRepository` 按用户采样间隔调用 `getTrafficData()`。
3. Repository 根据前后字节计数和时间差计算上下行速度。
4. 结果写入 `StateFlow<NetSpeedData>`。
5. `NetworkMonitorService` 收集速度状态：
   - 更新通知或 Live Update。
   - 更新或隐藏 Overlay。
6. 主界面直接观察相同 StateFlow 展示实时值。

## 4. 设置数据流

1. `DataStoreRepository` 定义并读写全部 Preferences Key。
2. `NetworkRepository` 将设置映射为长期 `StateFlow`。
3. ViewModel 将状态暴露给 Compose UI，并将修改请求委托给 Repository。
4. Service、NotificationHelper 和 OverlayWindow 读取相同状态，保证配置一致。

## 5. 主要组件

### SpeedDataSource

负责网络识别和接口字节计数，不负责速度差值、UI 或持久化。

### NetworkRepository

负责网速采样、差值计算和应用级设置状态。它是 Service 与 UI 的共享状态中心。

### SpeedFormatter

无状态格式化组件，统一主页、通知 Bitmap、Live Update、Overlay 和设置阈值中的数值精度与单位。

### NetworkMonitorService

负责长期运行、采样生命周期、通知发布和 Overlay 更新，不承载设置页面逻辑。

### MainViewModel / SettingsViewModel

负责 UI 事件、权限状态与 Repository 交互。Activity 只保留系统 Launcher、Intent 和 Compose 宿主职责。

## 6. 依赖注入

`di/AppModule.kt` 是 Koin 注册入口。Android 系统服务、DataStore、Repository、通知辅助类和 OverlayWindow 均由 Koin 提供。

## 7. 关键约束

- 不使用 Root 或 Shizuku。
- 不通过接口名黑名单判断 VPN。
- Foreground Service 运行时必须保留通知。
- 权限申请必须由用户可见操作触发。
- 代码中的版本、Key 和系统行为优先于文档描述。
