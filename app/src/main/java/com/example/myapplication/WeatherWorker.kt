package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices

class WeatherWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var latitude = 0.0
    var longitude = 0.0
    private var gotLocation: Boolean = false
    override fun doWork(): Result {

        val fetch = FetchWeather()

            getLastKnownLocation()

            var time = 0
            while (!gotLocation && time <= 5000) {
                Thread.sleep(1)
                time++
                if (time >= 5000) {
                    return Result.retry()
                }
            }

            fetch.fetchWeather(latitude, longitude)

            return Result.Success()

        }

        @SuppressLint("MissingPermission") //todo add permission check
        fun getLastKnownLocation() {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                        gotLocation = true
                    }
                }
        }

}