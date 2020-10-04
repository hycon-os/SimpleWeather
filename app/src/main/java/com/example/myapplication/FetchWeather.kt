package com.example.myapplication

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class FetchWeather {

    private val apiKey = "a9a5a8c0a12e5b11ae2fc673c8edf0c2"

    interface Getweather {
        @GET("weather?")
        fun getCurrentWeatherData(
            @Query("lat") lat: String?,
            @Query("lon") lon: String?,
            @Query("units") units : String?,
            @Query("appid") api : String?
        ): Call<JsonObject?>
    }

    fun fetchWeather(latitude: Double, longitude: Double) {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service =
            retrofit.create(
                Getweather::class.java
            )
        val call: Call<JsonObject?>? = service.getCurrentWeatherData(latitude.toString(),longitude.toString(),"metric",apiKey)

        call!!.enqueue(object : Callback<JsonObject?> {
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                return
            }

            override fun onResponse(
                call: Call<JsonObject?>,
                response: Response<JsonObject?>
            ) {
                if (response.code() == 200) {
                    val main = response.body()?.getAsJsonObject("main")
                    val sys = response.body()?.getAsJsonObject("sys")
                    val weather = response.body()?.getAsJsonArray("weather")?.get(0)?.asJsonObject
                    var sunset = sys?.get("sunset")!!.asLong
                    var sunrise = sys?.get("sunrise").asLong
                    var id = weather?.get("id")!!.asInt
                    var temp = main?.get("temp")
                    var icon = mapConditionIconToCode(id, sunrise, sunset)
                    println(icon)
                }
            }
        })

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