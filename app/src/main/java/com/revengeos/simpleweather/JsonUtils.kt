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

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.revengeos.simpleweather.data.WeatherData
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
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