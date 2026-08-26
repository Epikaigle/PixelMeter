# 通知与 Live Update

## 1. Foreground Service 基础通知

监听服务运行时 Android 强制要求常驻通知。动态网速关闭后，NotificationHelper 会发布最简基础通知，而不是完全移除通知。

通知频道使用稳定的 `CHANNEL_ID`，频道名称和描述来自本地化资源。频道关闭、声音和重要性最终由系统设置控制。

## 2. Bitmap 动态图标

普通通知模式使用 `IconCompat.createWithBitmap()`：

1. `SpeedFormatter` 将总网速拆分为数值和单位。
2. Canvas 使用两种 Paint 绘制数值与单位。
3. 用户可调整数值和单位字号比例。
4. 小图标始终代表总网速。

通知正文支持：

- 总速双向展示。
- 仅上行。
- 仅下行。
- 自定义上下行前缀和顺序。

## 3. 低流量模式

当总速低于 `key_notification_threshold`：

- 静态模式：使用应用静态图标和监听文案。
- 动态模式：继续显示低速数值，但不使用 Live Update。

阈值为 0 时关闭低流量判断。

## 4. Live Update

Android 16+ 可启用 Live Update：

- 使用静态 `ic_speed` Small Icon。
- 通过 `setShortCriticalText()` 显示短网速文本。
- 调用 `setRequestPromotedOngoing(true)` 请求 Promoted Ongoing 展示。
- 需要普通通知权限和系统支持；Manifest 声明 `POST_PROMOTED_NOTIFICATIONS`。

系统是否最终提升展示由 Android 决定。

## 5. 格式化

`SpeedFormatter` 是唯一网速格式来源，确保主页、通知、Live Update、Overlay 和阈值摘要一致。

固定单位按数值范围显示 0、1 或 2 位小数；Auto 模式按 B/s、KB/s、MB/s、GB/s 自动切换。最低显示单位可将较小流量显示为 0。

## 6. 更新去重

NotificationHelper 生成只包含可见状态的 Fingerprint。Service 只有在 Fingerprint 改变时调用 `NotificationManager.notify()`，避免相同通知重复刷新和状态栏图标排序抖动。

## 7. 真机验证

- Android 12/12L 基础通知启动。
- Android 13+ 通知授权和拒绝。
- Android 16+ Live Update。
- 浅色/深色状态栏可读性。
- 自定义颜色在不同 ROM 上的表现。
- 多常驻通知并存时的刷新稳定性。
