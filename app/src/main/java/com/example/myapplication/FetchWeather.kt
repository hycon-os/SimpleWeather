package com.example.myapplication

import android.content.Context
import com.example.myapplication.data.WeatherData
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.File


class FetchWeather(val context :Context) {

    private val apiKey = "a9a5a8c0a12e5b11ae2fc673c8edf0c2"
    private val utils = JsonUtils()
    private val file = File(context.getCacheDir(), "").toString() + "cacheFile.srl"
    var done = false

    interface Getweather {
        @GET("weather?")
        fun getCurrentWeatherData(
            @Query("lat") lat: String?,
            @Query("lon") lon: String?,
            @Query("units") units: String?,
            @Query("appid") api: String?
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

                    var json = Gson().fromJson(response.body(), WeatherData::class.java)
                    utils.save(file, json)
                    done = true
                }
            }
        })
    }

    fun getWeatherFromCache(): WeatherData? {
        return utils.load(file)
    }
}