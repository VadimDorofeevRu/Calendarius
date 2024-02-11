package ru.vadimdorofeev.calendarius

import android.annotation.SuppressLint
import android.content.Context

class Common {

    companion object {

        @SuppressLint("StaticFieldLeak")
        internal lateinit var context: Context

        // Локализованные названия дней недели
        internal lateinit var dayNames: Array<String>

        // Локализованные названия месяцев
        internal lateinit var monthNames: Array<String>
    }
}