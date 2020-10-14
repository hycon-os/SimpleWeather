/*
 * Copyright (C) 2020 RevengeOS
 * Copyright (C) 2020 Ethan Halsall
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.revengeos.simpleweather

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.work.*
import java.util.concurrent.TimeUnit


class SettingsActivity : AppCompatActivity() {
    private var isNotGranted: Boolean = true
    private lateinit var workManager: WorkManager
    private lateinit var sharedPreferences: SharedPreferences
    private val workerTag = "WeatherWorker"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        workManager = WorkManager.getInstance(this)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val button = findViewById<Button>(R.id.grant_permission)
        val permissionScreen = findViewById<LinearLayout>(R.id.location_permission_screen)

        sharedPreferences.registerOnSharedPreferenceChangeListener { prefs, key ->
            updatePrefs()
        }

        isNotGranted = checkPermission()
        permissionScreen.visibility = View.GONE

        if (checkPermission()) {
            permissionScreen.visibility = View.VISIBLE
            button.setOnClickListener { acquirePermissions() }
        } else {
            updatePrefs()
        }

    }

    private fun updatePrefs() {
        val isEnabled = sharedPreferences.getBoolean("weather_enabled", false)
        if (isEnabled) {
            updateWeather()
        } else {
            cancelWork()
        }
    }

    private fun cancelWork() {
        workManager.cancelAllWorkByTag(workerTag)
    }

    override fun onResume() {
        if (isNotGranted && !checkPermission()) {
            val intent = intent
            finish()
            startActivity(intent)
        }
        super.onResume()
    }

    private fun checkPermission(): Boolean {

        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun acquirePermissions() {

        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )

        ActivityCompat.requestPermissions(this, permissions, 0)
    }

    private fun updateWeather() {

        val request = PeriodicWorkRequestBuilder<WeatherWorker>(
            20,
            TimeUnit.MINUTES
        ).setBackoffCriteria(
            BackoffPolicy.LINEAR,
            PeriodicWorkRequest.DEFAULT_BACKOFF_DELAY_MILLIS,
            TimeUnit.MILLISECONDS
        ).build()
        workManager.enqueueUniquePeriodicWork(
            workerTag,
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }


    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}