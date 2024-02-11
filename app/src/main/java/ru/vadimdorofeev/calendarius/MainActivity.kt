package ru.vadimdorofeev.calendarius

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    private var year: Int = 0

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {

        Common.context = this

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        AppCompatDelegate.setDefaultNightMode(
            when (prefs.getString("darkmode", "system")) {
                "on" -> AppCompatDelegate.MODE_NIGHT_YES
                "off" -> AppCompatDelegate.MODE_NIGHT_NO
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Common.dayNames = resources.getStringArray(R.array.day_names)
        Common.monthNames = resources.getStringArray(R.array.month_names)

        Coloring.reloadColors()
        findViewById<FrameLayout>(R.id.main_root).setBackgroundColor(Coloring.colorBg)

        Dating.prepareWeekends()

        MonthView.onLongPress = { year, month ->
            val dialogContent: View = layoutInflater.inflate(R.layout.fragment_month, null)

            val pagerMonth = dialogContent.findViewById<ViewPager2>(R.id.viewpager_month)
            pagerMonth.setBackgroundColor(Coloring.colorBg)
            pagerMonth.adapter = MonthPagerViewAdapter(this, year)
            pagerMonth.post { pagerMonth.setCurrentItem(year * 12 + month - 1, false) }

            val d = Dialog(this)
            d.setContentView(dialogContent)
            d.show()
        }

        year = savedInstanceState?.getInt("year") ?: Calendar.getInstance().get(Calendar.YEAR)

        val pageYearAdapter = YearPagerViewAdapter(this)
        pagerYear.adapter = pageYearAdapter
        pagerYear.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position != 0) {
                    super.onPageSelected(position)
                    year = position
                    textviewYear.text = position.toString()
                }
            }
        })
        pagerYear.post { pagerYear.setCurrentItem(year, false) }

        val ibSettings = findViewById<ImageButton>(R.id.imagebutton_settings)
        ibSettings.setOnClickListener {
            settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
        }
    }

    private var settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        recreate()
    }

    private val textviewYear by lazy { findViewById<TextView>(R.id.textview_year) }
    private val pagerYear by lazy { findViewById<ViewPager2>(R.id.viewpager_year) }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("year", year)
    }
}