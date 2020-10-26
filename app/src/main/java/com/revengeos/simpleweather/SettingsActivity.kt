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
import android.app.Activity
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceFragment
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.work.WorkManager


class SettingsActivity : Activity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var isNotGranted: Boolean = true
    private lateinit var workManager: WorkManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var alarmScheduler: AlarmScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        fragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        actionBar?.setDisplayHomeAsUpEnabled(true)

        workManager = WorkManager.getInstance(this)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        alarmScheduler = AlarmScheduler(this)

        val button = findViewById<Button>(R.id.grant_permission)
        val permissionScreen = findViewById<LinearLayout>(R.id.location_permission_screen)

        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        isNotGranted = checkPermission()
        permissionScreen.visibility = View.GONE

        if (checkPermission()) {
            permissionScreen.visibility = View.VISIBLE
            button.setOnClickListener { acquirePermissions() }
        } else {
            updatePrefs()
        }
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        preference: String?
    ) {
        updatePrefs()
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
        alarmScheduler.cancelAlarms()
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (isNotGranted && !checkPermission()) {
            val intent = intent
            finish()
            startActivity(intent)
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
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
        alarmScheduler.scheduleAlarm()
    }


        class SettingsFragment : PreferenceFragment() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

}