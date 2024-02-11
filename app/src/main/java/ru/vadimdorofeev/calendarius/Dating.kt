package ru.vadimdorofeev.calendarius

import androidx.preference.PreferenceManager
import java.util.Calendar

class Dating {

    companion object {
        internal enum class DateKind { Exact, Easter }

        internal data class Holiday(
            val kind: DateKind
        ) {
            var month = 0
            var day = 0
            var offset = 0
        }

        private val holidays = mapOf(
            "ru" to arrayOf(
                Holiday(DateKind.Exact).apply { month = 1; day = 1 },
                Holiday(DateKind.Exact).apply { month = 1; day = 2 },
                Holiday(DateKind.Exact).apply { month = 1; day = 3 },
                Holiday(DateKind.Exact).apply { month = 1; day = 4 },
                Holiday(DateKind.Exact).apply { month = 1; day = 5 },
                Holiday(DateKind.Exact).apply { month = 1; day = 6 },
                Holiday(DateKind.Exact).apply { month = 1; day = 7 },
                Holiday(DateKind.Exact).apply { month = 1; day = 8 },
                Holiday(DateKind.Exact).apply { month = 2; day = 23 },
                Holiday(DateKind.Exact).apply { month = 3; day = 8 },
                Holiday(DateKind.Exact).apply { month = 5; day = 1 },
                Holiday(DateKind.Exact).apply { month = 5; day = 9 },
                Holiday(DateKind.Exact).apply { month = 6; day = 12 },
                Holiday(DateKind.Exact).apply { month = 11; day = 4 }
            ),
            "by" to arrayOf(
                Holiday(DateKind.Exact).apply { month = 1; day = 1 },
                Holiday(DateKind.Exact).apply { month = 1; day = 2 },
                Holiday(DateKind.Exact).apply { month = 1; day = 7 },
                Holiday(DateKind.Exact).apply { month = 3; day = 8 },
                Holiday(DateKind.Easter).apply { offset = 9 },
                Holiday(DateKind.Exact).apply { month = 5; day = 1 },
                Holiday(DateKind.Exact).apply { month = 5; day = 9 },
                Holiday(DateKind.Exact).apply { month = 7; day = 3 },
                Holiday(DateKind.Exact).apply { month = 11; day = 7 },
                Holiday(DateKind.Exact).apply { month = 12; day = 25 }
            )
        )

        private val preparedDates = mutableMapOf<String, MutableList<Int>>()

        private val easterDates = mutableMapOf<Int, Calendar>()

        private fun getEaster(year: Int): Calendar {
            val a = (19 * (year % 19) + 15) % 30
            val b = (2 * (year % 4) + 4 * (year % 7) + 6 * a + 6) % 7
            val f = a + b

            val cal = Calendar.getInstance()
            if (f >= 9)
                cal.set(year, 4 - 1, f - 9)
            else
                cal.set(year, 3 - 1, 22 + f)
            cal.add(Calendar.DATE, 13)
            return cal
        }

        private var weekends = mutableSetOf<String>()

        internal fun prepareWeekends() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(Common.context)
            weekends = prefs.getStringSet("weekends", null) ?: mutableSetOf("sunday")
        }

        internal fun isHoliday(country: String, year: Int, month: Int, day: Int, dow: Int): Boolean {
            if ((dow == 7 && weekends.contains("saturday")) ||
                (dow == 6 && weekends.contains("friday")) ||
                (dow == 5 && weekends.contains("thursday")) ||
                (dow == 4 && weekends.contains("wednesday")) ||
                (dow == 3 && weekends.contains("tuesday")) ||
                (dow == 2 && weekends.contains("monday")) ||
                (dow == 1 && weekends.contains("sunday")))
                return true

            val key = "$country-$year"
            if (!preparedDates.containsKey(key)) {
                val dates = mutableListOf<Int>()
                if (holidays.containsKey(country)) {
                    for (h in holidays[country]!!)
                        if (h.kind == DateKind.Exact)
                            dates.add(h.month * 100 + h.day)
                        else if (h.kind == DateKind.Easter) {
                            if (!easterDates.containsKey(year))
                                easterDates[year] = getEaster(year)
                            val cal = easterDates[year]!!.clone() as Calendar
                            cal.add(Calendar.DATE, h.offset)
                            dates.add((cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DATE))
                        }
                }
                preparedDates[key] = dates
            }
            return preparedDates[key]!!.contains(month * 100 + day)
        }
    }
}