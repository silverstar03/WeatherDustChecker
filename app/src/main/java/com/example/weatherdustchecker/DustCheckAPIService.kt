package com.example.weatherdustchecker

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.reflect.Type

interface DustCheckAPIService {
    @GET("/feed/geo:{lat};{lon}/")
    fun getDustStatusInfo(
        @Path("lat") lat: Double,
        @Path("lon") lon: Double,
        @Query("token") token: String
    ) : Call<DustCheckResponseFromGSON>
}

data class DustCheckResponseFromGSON(val pm10: Int, val pm25: Int, val pm10Status : String, val pm25Status : String)

class DustCheckerResponseDeserializerGSON : JsonDeserializer<DustCheckResponseFromGSON> {
    private val checkCategory = { aqi : Int -> when(aqi) {
        in (0 .. 100) -> "좋음"
        in (101 .. 200) -> "보통"
        in (201 .. 300) -> "나쁨"
        else -> "매우 나쁨"
    }}

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): DustCheckResponseFromGSON {
        val root = json?.getAsJsonObject()

        val dataNode = root?.getAsJsonObject("data")

        var iaqiNode = dataNode?.getAsJsonObject("iaqi")
        var pm10Node = iaqiNode?.getAsJsonObject("pm10")
        var pm25Node = iaqiNode?.getAsJsonObject("pm25")

        var pm10 = pm10Node?.get("v")?.asInt
        var pm25 = pm25Node?.get("v")?.asInt

        return DustCheckResponseFromGSON(pm10!!, pm25!!, checkCategory(pm10), checkCategory(pm25))
    }
}


















