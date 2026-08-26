# Foreground Service 生命周期

## 1. 配置

`NetworkMonitorService` 在 Manifest 中声明：

```xml
android:foregroundServiceType="specialUse|dataSync"
```

并声明 `network_monitor` 的 Special Use 子类型。

运行时：

- Android 14+：`FOREGROUND_SERVICE_TYPE_SPECIAL_USE`
- 更低的受支持版本：`FOREGROUND_SERVICE_TYPE_DATA_SYNC`

## 2. 启动来源

服务可能由以下入口启动：

- 主界面启动按钮。
- 首次设置向导“完成并启动”。
- Quick Settings Tile。
- `BootReceiver`，前提是用户启用了自动启动。

Android 13+ 启动前检查 `POST_NOTIFICATIONS`。Android 12/12L 没有该运行时权限，不得因为权限检查而阻止启动。

Overlay 已启用时还需检查 `Settings.canDrawOverlays()`。

## 3. 首次通知

`onStartCommand()` 必须立即调用 `startForeground()`。首次通知直接读取当前 Repository 配置：

- 动态通知开关。
- 上下行前缀和顺序。
- 显示模式。
- 数字与单位字号。
- 低流量阈值和动作。
- 自定义颜色。
- 网速单位和最低显示单位。

即使用户关闭动态通知网速，Foreground Service 仍需要基础常驻通知。

## 4. 运行阶段

Service 启动后：

1. 调用 `NetworkRepository.startMonitoring()`。
2. 收集 `netSpeed` StateFlow。
3. 在主线程更新 Overlay。
4. 在后台线程构建通知。
5. 使用通知展示指纹避免重复发布可见内容相同的通知。

通知支持：

- 基础静态通知。
- Bitmap 动态小图标。
- Android 16+ Live Update。

## 5. 屏幕休眠策略

- 收到 `ACTION_SCREEN_OFF` 后启动 2 分钟倒计时。
- 倒计时结束仍未亮屏时停止 Repository 采样，但保留 Service。
- 收到 `ACTION_SCREEN_ON` 后取消倒计时；若已暂停则恢复采样。

此策略减少息屏后的持续计算，同时保证亮屏后恢复网速显示。

## 6. 开机启动

`BootReceiver` 监听：

- `BOOT_COMPLETED`
- `QUICKBOOT_POWERON`

仅当 `key_auto_start_service` 为 true 时调用 `startForegroundService()`。启动异常会记录日志并被捕获。

## 7. 停止与释放

Service 销毁时：

- 取消速度收集 Job。
- 取消息屏延迟 Job。
- 隐藏并释放 Overlay。
- 停止 Repository 采样。
- 移除 Foreground Notification。
- 注销屏幕广播 Receiver。

## 8. Android 系统限制

- Android 14+ 对后台启动 Foreground Service 有严格限制。
- 权限申请和普通启动应由可见 UI 或系统允许的入口触发。
- `POST_PROMOTED_NOTIFICATIONS` 只用于可选 Live Update，不替代普通通知权限。
- 修改启动来源、类型或保活策略时必须检查目标 SDK 行为和 Google Play 政策。
