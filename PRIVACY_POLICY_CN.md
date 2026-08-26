# Pixel Meter 隐私政策

**生效日期：2026-08-26**

Pixel Meter（以下简称“我们”）重视用户隐私。本政策说明应用如何处理信息以及 Android 权限的具体用途。

## 1. 信息收集与本地处理

**Pixel Meter 不会收集、存储、出售或传输个人数据、设备标识符、网络流量内容、浏览记录或应用使用数据。**

应用只读取 Android 提供的本地物理网络接口字节计数器，用于在设备上计算当前上传和下载速度。Pixel Meter 不会检查数据包内容、DNS 请求、访问的网站或通信内容。

用户设置通过 Android Preferences DataStore 保存在设备本地。

## 2. 权限说明

Pixel Meter 可能声明或申请以下权限：

- **`ACCESS_NETWORK_STATE`**：识别当前网络 Transport 和物理接口名称，并在网速计算中排除 VPN Transport。
- **`INTERNET`**：支持用户主动打开的网页，包括 Cloudflare 网络测速；不会用于统计或后台上报。
- **`FOREGROUND_SERVICE`**：运行由用户启用的实时网速监听服务。
- **`FOREGROUND_SERVICE_SPECIAL_USE`**：声明 Android 14+ 使用的网络监听 Foreground Service 子类型。
- **`FOREGROUND_SERVICE_DATA_SYNC`**：为较低的受支持 Android 版本提供兼容的 Foreground Service 类型。
- **`POST_NOTIFICATIONS`**：Android 13+ 显示监听服务通知所需的运行时权限。
- **`POST_PROMOTED_NOTIFICATIONS`**：用于用户主动启用且系统支持的 Android 16+ Live Update 展示。
- **`SYSTEM_ALERT_WINDOW`**：仅在用户启用悬浮窗时申请，用于在其他应用上方显示网速。
- **`RECEIVE_BOOT_COMPLETED`**：仅在用户明确启用自动启动后，于设备开机完成时启动监听服务。
- **`REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`**：用户点击对应设置项时打开系统电池优化豁免申请页面，是否授权完全由用户决定。

Android 要求 Foreground Service 运行期间显示常驻通知。即使关闭动态通知网速，也会保留最简服务通知。

## 3. 第三方网页

Pixel Meter 不包含第三方统计、广告 SDK 或追踪库。

用户主动操作时，应用可能通过 Chrome Custom Tabs 或其他浏览器打开以下外部页面：

- `speed.cloudflare.com`
- GitHub
- Pixel Tailor
- Telegram
- `dontkillmyapp.com`

这些页面受各自的隐私政策和所选浏览器的隐私设置约束。Pixel Meter 不会从这些页面接收浏览数据。

## 4. 数据共享与保留

Pixel Meter 不运营用于保存应用数据的服务器，也不会向第三方共享用户数据。本地设置会保留在设备中，直到用户清除应用数据或卸载应用。

## 5. 儿童隐私

Pixel Meter 不面向 13 岁以下儿童，也不会有意收集儿童的个人信息。

## 6. 政策变更

当应用功能、权限或法律要求发生变化时，我们可能更新本政策。最新版本会随项目和相关分发页面发布。

## 7. 联系方式

如有问题或建议，请通过 [Pixel Meter GitHub 项目](https://github.com/Pixel-Tailor-CN/PixelMeter/issues) 提交 Issue。
