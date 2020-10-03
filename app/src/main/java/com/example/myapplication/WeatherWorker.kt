package com.example.myapplication

import android.content.Context
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit


class WeatherWorker{

    /* init {
        val periodicWork =
            PeriodicWorkRequest.Builder(UpdateWeather::class.java, 1, TimeUnit.SECONDS)
                .build()
        WorkManager.getInstance().enqueue(periodicWork)
    } */

    class UpdateWeather(context: Context, workerParams: WorkerParameters) : Worker(context,
        workerParams
    ) {
        override fun doWork(): Result {

            val fetch = FetchWeather()

            fetch.fetchWeather(5.0,10.0)

            // Indicate success or failure with your return value:
            return Result.Success()

            // (Returning RETRY tells WorkManager to try this task again
            // later; FAILURE says not to try again.)
        }
    }
    
}