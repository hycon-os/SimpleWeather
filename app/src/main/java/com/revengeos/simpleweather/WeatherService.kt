package com.revengeos.simpleweather

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class WeatherService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val periodicWork =
            PeriodicWorkRequest.Builder(WeatherWorker::class.java, 15, TimeUnit.MINUTES)
                .build()
        WorkManager.getInstance().enqueue(periodicWork)
        return super.onStartCommand(intent, flags, startId)
    }
}