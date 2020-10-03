package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val periodicWork =
            PeriodicWorkRequest.Builder(WeatherWorker.UpdateWeather::class.java, 15, TimeUnit.MINUTES)
                .build()
        WorkManager.getInstance().enqueue(periodicWork)
    }
}