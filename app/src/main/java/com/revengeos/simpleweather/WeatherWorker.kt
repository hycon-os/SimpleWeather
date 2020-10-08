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

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices

class WeatherWorker(val context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var latitude = 0.0
    var longitude = 0.0
    private var gotLocation: Boolean = false
    private val intent: Intent = Intent("org.revengeos.simpleweather.update")
    override fun doWork(): Result {

        val fetch = FetchWeather(context)

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

        time = 0
        while (!fetch.done && time <= 5000) {
            Thread.sleep(1)
            time++
            if (time >= 5000) {
                return Result.retry()
            }
        }

        val data = fetch.getWeatherFromCache()
        val id = data!!.weather.get(0).id
        val sunrise = data.sys.sunrise
        val sunset = data.sys.sunset
        val temp = data.main.temp.toInt()
        val icon = mapConditionIconToCode(id, sunrise, sunset)
        intent.putExtra("temp", temp)
            .putExtra("icon", icon)
        context.sendBroadcast(intent)

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

    private fun mapConditionIconToCode(conditionId: Int, sunrise: Long, sunset: Long): Int {
        val ut2 = System.currentTimeMillis() / 1000L
        if (sunrise < ut2 && sunset > ut2) {
            // First, use condition ID for specific cases
            when (conditionId) {
                202, 232, 211 -> return 4
                212 -> return 3
                221, 231, 201 -> return 38
                230, 200, 210 -> return 37
                300, 301, 302, 310, 311, 312, 313, 314, 321 -> return 9
                500, 501, 520, 521, 531 -> return 11
                502, 503, 504, 522 -> return 12
                511 -> return 10
                600, 620 -> return 14 // light snow
                601, 621 -> return 16 // snow
                602, 622 -> return 41 // heavy snow
                611, 612 -> return 18 // sleet
                615, 616 -> return 5 // rain and snow
                741 -> return 20
                711, 762 -> return 22
                701, 721 -> return 21
                731, 751, 761 -> return 19
                771 -> return 23
                781 -> return 0
                800 -> return 32
                801 -> return 34
                802 -> return 28
                803, 804 -> return 30
                900 -> return 0 // tornado
                901 -> return 1 // tropical storm
                902 -> return 2 // hurricane
                903 -> return 25 // cold
                904 -> return 36 // hot
                905 -> return 24 // windy
                906 -> return 17 // hail
            }
        } else {
            // First, use condition ID for specific cases
            when (conditionId) {
                202, 232, 211 -> return 4
                212 -> return 3
                221, 231, 201 -> return 47
                230, 200, 210 -> return 45
                300, 301, 302, 310, 311, 312, 313, 314, 321 -> return 9
                500, 501, 520, 521, 531 -> return 11
                502, 503, 504, 522 -> return 12
                511 -> return 10
                600, 620 -> return 14 // light snow
                601, 621 -> return 16 // snow
                602, 622 -> return 41 // heavy snow
                611, 612 -> return 18 // sleet
                615, 616 -> return 5 // rain and snow
                741 -> return 20
                711, 762 -> return 22
                701, 721 -> return 21
                731, 751, 761 -> return 19
                771 -> return 23
                781 -> return 0
                800 -> return 31
                801 -> return 33
                802 -> return 27
                803, 804 -> return 29
                900 -> return 0 // tornado
                901 -> return 1 // tropical storm
                902 -> return 2 // hurricane
                903 -> return 25 // cold
                904 -> return 36 // hot
                905 -> return 24 // windy
                906 -> return 17 // hail
            }
        }
        return -1
    }

}