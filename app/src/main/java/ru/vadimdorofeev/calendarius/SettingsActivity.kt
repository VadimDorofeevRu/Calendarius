package ru.vadimdorofeev.calendarius

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<FrameLayout>(R.id.settings).setBackgroundColor(Coloring.colorBg)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        @Suppress("UNCHECKED_CAST")
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val lp = findPreference<ListPreference>("theme")
            lp!!.entries = Coloring.getThemesNames()
            lp.entryValues = Coloring.getThemesCodes()

            val mlp = findPreference<MultiSelectListPreference>("weekends")
            mlp!!.summary = getDaysString(mlp.values)
            mlp.setOnPreferenceChangeListener { _, newValue ->
                mlp.summary = getDaysString(newValue as Set<String>)
                true
            }

            val pp = findPreference<Preference>("privacyPolicy")
            pp?.setOnPreferenceClickListener {
                val dialog = AlertDialog.Builder(requireContext())
                    .setMessage(R.string.settings_privacy_policy_text)
                    .setTitle(R.string.settings_privacy_policy_title)
                    .setPositiveButton(R.string.settings_privacy_policy_close) { dialog, _ -> dialog.dismiss() }
                    .setNeutralButton(R.string.settings_privacy_policy_visit_site) { _, _ ->
                        val siteIntent = Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.vadimdorofeev.ru/legal/gp-privacy-calendarius.html"))
                        startActivity(siteIntent)
                    }
                    .create()
                dialog.show()
                true
            }
        }

        private fun getDaysString(codes: Set<String>): String {
            return "" +
                (if (codes.contains("monday"))    "${resources.getString(R.string.day_monday)} "    else "") +
                (if (codes.contains("tuesday"))   "${resources.getString(R.string.day_tuesday)} "   else "") +
                (if (codes.contains("wednesday")) "${resources.getString(R.string.day_wednesday)} " else "") +
                (if (codes.contains("thursday"))  "${resources.getString(R.string.day_thursday)} "  else "") +
                (if (codes.contains("friday"))    "${resources.getString(R.string.day_friday)} "    else "") +
                (if (codes.contains("saturday"))  "${resources.getString(R.string.day_saturday)} "  else "") +
                (if (codes.contains("sunday"))    "${resources.getString(R.string.day_sunday)} "    else "")
        }
    }
}