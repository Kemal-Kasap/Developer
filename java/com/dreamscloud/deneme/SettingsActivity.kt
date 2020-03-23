package com.dreamscloud.deneme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat

/**
 * This activity start when clicked on settings.
 */
class SettingsActivity : AppCompatActivity() {

    /**
     * When setting activity started
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    /**
     * Settings Fragment
     */
    class SettingsFragment : PreferenceFragmentCompat() {
        /**
         * When settings fragment created
         */
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)


        }
    }
}