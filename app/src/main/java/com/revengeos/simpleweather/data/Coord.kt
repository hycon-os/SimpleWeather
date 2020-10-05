package com.revengeos.simpleweather.data


import com.google.gson.annotations.SerializedName

data class Coord(
    @SerializedName("lat")
    val lat: Int,
    @SerializedName("lon")
    val lon: Int
)