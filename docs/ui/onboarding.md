# 首次设置向导

## 1. 目标

Onboarding 帮助新用户理解 VPN 去重能力、选择显示方式并授予必要权限。它不应在用户未确认时自动启用任何显示方式。

## 2. 展示规则

- 新安装且 Preferences 为空：自动展示一次。
- 已存在 Preferences 的旧用户：升级后不自动展示。
- 用户跳过或完成后写入 `key_onboarding_shown=true`。
- 设置 → General 提供重新运行向导入口。

## 3. 三步流程

### Step 1：产品说明

说明应用用途和通过物理接口避免 VPN 重复统计的核心价值。

操作：

- 开始设置。
- 稍后设置。
- TopAppBar 跳过。

### Step 2：显示方式

默认全部关闭：

- 通知网速。
- Live Update。
- Overlay。

推荐方案：

- Android 16+：通知网速 + Live Update。
- 较低版本：通知网速。

关闭通知网速时同时关闭 Live Update。Live Update 在不支持的版本上不可操作。

页面需要说明：Foreground Service 运行时始终需要基础通知。

### Step 3：权限与完成

通知或 Overlay 任一被选择时，监听服务都需要通知权限；Overlay 还需要显示在其他应用上层权限。

- 所需权限全部授予：显示“完成并启动”。
- 没有选择任何显示方式：显示“完成”，不启动服务。
- 权限未完成：主按钮禁用，提供“稍后完成”，保存选择但不启动。

## 4. 数据写入

完成时一次保存：

- `key_onboarding_shown`
- `key_notification_enabled`
- `key_live_update`
- `key_overlay_enabled`

完成并启动时，Repository 先更新持久化与 StateFlow，再由 MainViewModel 启动 Service，避免服务读取旧配置。

## 5. 生命周期

MainActivity 通过 Intent Extra 接收“重新运行向导”请求。Extra 使用后立即移除，避免 Activity 重建时重复打开。

权限状态在 Activity `ON_RESUME` 时刷新，以处理从系统授权页返回的场景。

## 6. 文案和兼容性

- 所有文案必须来自 String Resource。
- 权限名称和 Android 版本限制应明确。
- 新增向导文案必须同步所有 Locale。
- 修改完成逻辑时应覆盖 Android 12/12L 与 Android 13+ 的通知权限差异。
