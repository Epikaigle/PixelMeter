# 国际化与本地化

## 1. 当前 Locale

资源目录当前包括：

- `values/`：默认英语
- `values-zh-rCN/`：简体中文
- `values-pt/`：葡萄牙语
- `values-pt-rBR/`：巴西葡萄牙语
- `values-ru/`：俄语

`app_name` 等品牌资源可以标记为不可翻译。

## 2. 默认语言

`app/src/main/res/resources.properties` 定义：

```properties
unqualifiedResLocale=en
```

因此 `values/strings.xml` 必须是完整英语回退资源。不能将只希望在中文环境显示的中文文案放入默认资源。

`translatable="false"` 仅表示字符串不进入翻译流程，不会限制它只在某个 Locale 显示。

## 3. Locale Config

`app/build.gradle.kts` 会：

1. 读取 `unqualifiedResLocale`。
2. 扫描存在 `strings.xml` 的 `values-*` 目录。
3. 校验 Android Locale qualifier。
4. 自动设置 `localeFilters`。
5. 使用 `generateLocaleConfig=true` 生成 App Locale Config。

新增语言时不需要手写 Locale Config，但目录名称必须符合 Android qualifier 规则。

## 4. Weblate

应用字符串通过 Weblate 托管。翻译提交可能由 Weblate 分支或 Pull Request 合并。

新增文案时：

- 使用稳定、语义明确的 String Key。
- 默认英语先表达完整含义。
- 保持 `%1$s`、`%1$d` 等参数数量和类型一致。
- 对 XML 特殊字符正确转义。
- 为所有当前 Locale 补齐翻译，否则 Lint 会报告 `MissingTranslation`。
- 不在 Kotlin/Compose 中硬编码面向用户或无障碍的文本。

## 5. 数值与单位

`SpeedFormatter` 使用系统 Locale 格式化小数，但网速单位保持技术缩写：

- B/s
- KB/s
- MB/s
- GB/s
- Live Update 中使用更短的 K/s、M/s、G/s

单位格式属于状态栏空间约束，不通过翻译资源改变。

## 6. RTL 与可访问性

Manifest 声明 `supportsRtl=true`。新增布局时应使用 Start/End 而非固定 Left/Right，除非选项明确表示物理方向。

图标按钮、通知频道描述、Tile 状态等无障碍文本必须来自 String Resource。

建议发布前使用伪语言或 RTL Locale 检查：

- 长文本截断。
- 设置页双栏和二级导航。
- Onboarding 滚动和按钮布局。
- Overlay 的 Start/End 对齐。
