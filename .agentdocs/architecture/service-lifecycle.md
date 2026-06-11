# 服务生命周期与保活 (Service Lifecycle)

为了保证网速指示器在后台持续运行并实时更新，App 必须维护一个稳定的前台服务 (Foreground Service)。

## 1. Service 配置

- **类名**: `NetworkMonitorService`
- **类型**: `Foreground Service`
- **foregroundServiceType**: Manifest 中声明 `specialUse|dataSync`，Android 14+ 运行时使用
  `FOREGROUND_SERVICE_TYPE_SPECIAL_USE`，更低版本使用 `FOREGROUND_SERVICE_TYPE_DATA_SYNC`。

```xml

<service
    android:name=".service.NetworkMonitorService"
    android:exported="false"
    android:foregroundServiceType="specialUse|dataSync">
    <property
        android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE"
        android:value="network_monitor" />
</service>
```

## 2. 启动与保活

### 2.1 启动流程

1. **用户开启**: 用户在主界面点击启动按钮。
2. **Context.startForegroundService()**: 启动服务。
3. **startForeground()**: 服务在 `onStartCommand` 中必须及时调用 `startForeground`
   ，绑定一个持续显示的 Notification，否则会被系统杀掉并抛出异常。

### 2.2 开机自启 (Auto-start)

- **触发机制**: 监听 `BOOT_COMPLETED` 和 `QUICKBOOT_POWERON` 广播。
- **条件判断**: `BootReceiver` 同步读取 `NetworkRepository.isAutoStartServiceEnabled.value`，仅当用户开启
  `key_auto_start_service` 时启动服务。
- **实现**: `BootReceiver` 在满足上述条件后调用 `startForegroundService`，异常时写入 Log，不向外抛出。

### 2.3 周期性任务 (Ticker)

- 使用 Kotlin Coroutines 实现周期性任务，默认采样间隔为 1500ms，用户可在设置页配置 1000ms 到 3000ms。
- **任务内容**:
    - 通过 `SpeedDataSource.getTrafficData()` 读取接口流量。
    - 在 `NetworkRepository` 中根据时间差计算上下行速率。
    - 生成 Notification Bitmap。
    - 更新 NotificationManager。
    - 通过 `StateFlow` 通知 UI 层（悬浮窗/主页），项目未使用 EventBus。

## 3. Android 14 (API 34) 适配

Android 14 对前台服务有严格限制：

- **权限声明**: Manifest 中声明了 `android.permission.FOREGROUND_SERVICE`、
  `android.permission.FOREGROUND_SERVICE_SPECIAL_USE` 和
  `android.permission.FOREGROUND_SERVICE_DATA_SYNC`。
- **运行时机**: 仅当 App 处于前台（Visible）时才能调用 `startForegroundService`。若 App 在后台尝试启动服务，会抛出
  `ForegroundServiceStartNotAllowedException`。
    - **处理策略**: 确保服务的启动操作仅由用户在 UI 界面手动触发，或者在 BootReceiver (开机自启)
      中依循系统允许的豁免规则进行。

## 4. 资源释放

- 当用户在主界面点击停止，系统会调用 `stopService()`；服务销毁时取消协程、隐藏悬浮窗、停止 Repository
  监听，并调用 `stopForeground(STOP_FOREGROUND_REMOVE)`。

## 5. 电量与性能优化 (Power Optimization)

为了避免长期占用 CPU 导致设备无法休眠，App 内置了智能休眠与唤醒机制：

### 5.1 屏幕状态监听

Service 内部通过 `BroadcastReceiver` 监听屏幕状态广播：

- `Intent.ACTION_SCREEN_OFF`: 屏幕关闭。
- `Intent.ACTION_SCREEN_ON`: 屏幕点亮。

### 5.2 智能休眠策略

1. **延迟停止**: 当检测到 **屏幕关闭** 时，Service 并不会立即停止监听（考虑到用户可能短按电源键），而是启动一个
   **2分钟** 的倒计时。
2. **进入休眠**: 若 2分钟内屏幕未重新点亮，Service 将主动停止网络监听协程 (`stopMonitoring`)，释放 CPU
   锁，允许设备进入深度休眠 (Doze Mode)。
3. **即时唤醒**: 当检测到 **屏幕点亮** 时：
    - 若仍在 2分钟倒计时内，直接取消倒计时，无缝继续。
    - 若已进入休眠状态，立即重启网络监听协程 (`startMonitoring`)，恢复网速更新。

此策略在保证用户体验（点亮屏幕即见网速）的同时，显著降低了待机功耗。
