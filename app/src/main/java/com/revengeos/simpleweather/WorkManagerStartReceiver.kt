package com.revengeos.simpleweather

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class WorkManagerStartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val periodicWork =
            PeriodicWorkRequest.Builder(WeatherWorker::class.java, 15, TimeUnit.MINUTES)
                .build()
        context?.let {
            WorkManager.getInstance(it).enqueueUniquePeriodicWork(
                "WeatherWorker",
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicWork
            )
        }
    }
}