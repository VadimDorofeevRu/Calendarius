package ru.vadimdorofeev.calendarius

import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager

class Coloring {

    companion object {

        internal class ColorTheme {
            var titleResId = 0
            var bgColors = Array(12) { 0 }
            var titleColors = Array(12) { 0 }
        }

        private val themes = mapOf(
            "classic" to ColorTheme().apply {
                titleResId = R.string.color_theme_classic
                bgColors = arrayOf(
                    0x808080, 0x808080, 0x808080, 0x808080, 0x808080, 0x808080,
                    0x808080, 0x808080, 0x808080, 0x808080, 0x808080, 0x808080)
                titleColors = arrayOf(
                    0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF,
                    0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF)
            },
            "climate" to ColorTheme().apply {
                titleResId = R.string.color_theme_climate
                bgColors = arrayOf(
                    0x40E0FF, 0x60F0FF, 0x80FFC0, 0x80FF80, 0xE0FF80, 0xFFF060,
                    0xFFE040, 0xFFF060, 0xE0FF80, 0xA0FF80, 0x80FFE0, 0x60F0FF)
                titleColors = arrayOf(
                    0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
                    0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000)
            },
            "spectrum" to ColorTheme().apply {
                titleResId = R.string.color_theme_spectrum
                bgColors = arrayOf(
                    0xFF4C4C, 0xFFA54D, 0xFFFF4D, 0xA6FF4D, 0x4DFF4D, 0x4DFFA6,
                    0x4DFFFF, 0x4DA6FF, 0x4D4DFF, 0xA64DFF, 0xFF4DFF, 0xFF4DA6
                )
                titleColors = arrayOf(
                    0xFFFFFF, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
                    0x000000, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF)
            }
        )

        private var currentTheme = themes["climate"]!!

        internal var colorBg: Int = 0
        internal var colorTodayBg: Int = 0
        internal var colorRegularDay: Int = 0
        internal var colorRegularDayPast: Int = 0
        internal var colorWeekend: Int = 0
        internal var colorWeekendPast: Int = 0

        private fun getCustomColor(id: Int): Int {
            val typedValue = TypedValue()
            Common.context.theme.resolveAttribute(id, typedValue, true)
            return ContextCompat.getColor(Common.context, typedValue.resourceId)
        }

        internal fun reloadColors() {
            colorBg = getCustomColor(R.attr.colorBg)
            colorTodayBg = getCustomColor(R.attr.colorTodayBg)
            colorRegularDay = getCustomColor(R.attr.colorRegularDay)
            colorRegularDayPast = getCustomColor(R.attr.colorRegularDayPast)
            colorWeekend = getCustomColor(R.attr.colorWeekend)
            colorWeekendPast = getCustomColor(R.attr.colorWeekendPast)

            val prefs = PreferenceManager.getDefaultSharedPreferences(Common.context)
            val theme = prefs.getString("theme", "climate")
            currentTheme = themes[theme]!!
        }

        internal fun getMonthBgColor(month: Int): Int {
            return (0xFF000000 + currentTheme.bgColors[month - 1]).toInt()
        }

        internal fun getMonthTextColor(month: Int): Int {
            return (0xFF000000 + currentTheme.titleColors[month - 1]).toInt()
        }

        internal fun getThemesNames(): Array<String> {
            return Array(themes.size) { n ->
                Common.context.resources.getString(themes[themes.keys.elementAt(n)]!!.titleResId) }
        }

        internal fun getThemesCodes(): Array<String> {
            return Array(themes.size) { n -> themes.keys.elementAt(n) }
        }
    }
}