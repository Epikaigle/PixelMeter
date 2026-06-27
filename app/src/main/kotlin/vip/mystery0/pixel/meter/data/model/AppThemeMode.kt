package vip.mystery0.pixel.meter.data.model

/**
 * 应用主题模式。
 *
 * value 会持久化到 DataStore，新增模式时必须保持已有数值不变，避免影响用户已有设置。
 */
enum class AppThemeMode(val value: Int) {
    Dynamic(0),
    Fixed(1);

    companion object {
        val DEFAULT_THEME_COLOR: Int = 0xFF006A66.toInt()

        fun fromValue(value: Int): AppThemeMode =
            entries.firstOrNull { it.value == value } ?: Dynamic
    }
}
