# Pixel Meter 项目文档

本目录保存 Pixel Meter 的长期架构、UI、维护流程和需求实施资料。代码、Manifest、资源目录和 `gradle/libs.versions.toml` 是最终事实来源；文档应随相关变更同步更新。

## 架构文档

- [总体架构](architecture/overview.md)：模块、依赖关系和主要数据流。
- [网络数据源](architecture/network-data-source.md)：物理接口识别、VPN 排除与 TrafficStats 读取。
- [服务生命周期](architecture/service-lifecycle.md)：Foreground Service、通知、休眠和开机启动。
- [偏好设置](architecture/preferences.md)：DataStore Key、默认值和升级兼容逻辑。
- [国际化](architecture/localization.md)：Locale Config、资源约束和 Weblate 流程。

## UI 文档

- [设计系统](ui/design-system.md)：Material 3、主题和响应式设置页。
- [首次设置向导](ui/onboarding.md)：引导流程、权限和完成行为。
- [通知显示](ui/notification.md)：Bitmap 图标、低流量模式和 Live Update。
- [悬浮窗](ui/overlay.md)：WindowManager、Compose 宿主和交互行为。

## 需求设计与实施计划

后续需求设计文档和实施计划统一放在 [`plans/`](plans/README.md)。

- 设计文档：`YYYY-MM-DD-主题-design.md`
- 实施计划：`YYYY-MM-DD-主题-plan.md`

## 图片

- `Screenshot_CN.png`：中文界面截图。
- `Screenshot_EN.png`：英文界面截图。
- `Component.png`：通知或组件效果展示。

## 维护要求

发生以下变化时必须检查对应文档：

- 修改网络接口过滤或网速计算。
- 修改 Foreground Service、权限、通知或后台行为。
- 新增、删除或迁移 DataStore Key。
- 修改 Onboarding、设置页、通知或 Overlay 交互。
- 新增 Locale 或调整翻译流程。
- 修改 SDK、Kotlin、AGP、Compose 或主要依赖。
