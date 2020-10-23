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

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class AlarmScheduler(private val context: Context) {
    private val REQUEST_CODE = 2344

    fun scheduleAlarm() {

        var workManager = context.let { WorkManager.getInstance(it) }

        val request = OneTimeWorkRequestBuilder<WeatherWorker>().build()
        workManager.enqueue(request)

        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WorkManagerStartReceiver::class.java)
        val alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0)

        // Cancel alarms before scheduling new one
        try {
            alarmMgr.cancel(alarmIntent)
        } catch (e: Exception) {
            Log.e("TAG", "AlarmManager update was not canceled. $e")
        }

        alarmMgr.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() +
                    15 * 60 * 1000, alarmIntent
        )

    }

    fun cancelAlarms() {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WorkManagerStartReceiver::class.java)
        val alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0)

        // Cancel alarms before scheduling new one
        try {
            alarmMgr.cancel(alarmIntent)
        } catch (e: Exception) {
            Log.e("TAG", "AlarmManager update was not canceled. $e")
        }
    }
}