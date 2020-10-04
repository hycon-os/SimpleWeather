package com.example.myapplication

import com.example.myapplication.data.WeatherData
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.*
import java.nio.charset.Charset


class JsonUtils {

    fun save(file: String?, data: WeatherData?) {
        try {
            OutputStreamWriter(
                FileOutputStream(file),
                Charset.forName("UTF-8")
            ).use { writer -> writer.write(Gson().toJson(data)) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun load(file: String?): WeatherData? {
        try {
            InputStreamReader(FileInputStream(file), Charset.forName("UTF-8")).use { freader ->
                val reader = JsonReader(freader)
                return Gson().fromJson(reader, WeatherData::class.java)
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}