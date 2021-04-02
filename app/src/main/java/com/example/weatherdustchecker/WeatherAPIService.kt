package com.example.weatherdustchecker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WeatherAPIService {
    @GET("/data/2.5/weather")
    fun getWeatherStatusInfo(
        // (3)
        @Query("appid") appId: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String="metric"
        // (4)
    ) : Call<OpenWeatherAPIJSONResponseFromGSON>
}

data class OpenWeatherAPIJSONResponseFromGSON(
    val main: Map<String, String>,
    val weather: List<Map<String, String>>)







