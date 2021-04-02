package com.example.weatherdustchecker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL


class WeatherPageFragment : Fragment() {
    @JsonIgnoreProperties(ignoreUnknown=true)
    class OpenWeatherAPIJSONResponse(
        val main: Map<String, String>,
        val weather: List<Map<String, String>>)

    lateinit var weatherImage : ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View? {
        val view =
            inflater.inflate(
                R.layout.weather_page_fragment,
                container,
                false)

        /*
        val weatherImage = view.findViewById<ImageView>(R.id.weather_icon)
        val statusText = view.findViewById<TextView>(R.id.weather_status_text)
        val tempText = view.findViewById<TextView>(R.id.weather_temp_text)

        weatherImage.setImageResource(arguments!!.getInt("res_id"))
        statusText.text = arguments!!.getString("status")
        tempText.text = arguments!!.getDouble("temp").toString()
        */

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherImage = view.findViewById<ImageView>(R.id.weather_icon)
        val statusText = view.findViewById<TextView>(R.id.weather_status_text)
        val tempText = view.findViewById<TextView>(R.id.weather_temp_text)

        val lat = arguments!!.getDouble("lat")
        val lon = arguments!!.getDouble("lon")

        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService : WeatherAPIService = retrofit.create(WeatherAPIService::class.java)

        val apiCallForData =
            apiService
                .getWeatherStatusInfo("871e998f753c9217f41114a8c5a5f0f6", lat, lon)

        apiCallForData.enqueue(object : Callback<OpenWeatherAPIJSONResponseFromGSON> {
            override fun onFailure(
                call: Call<OpenWeatherAPIJSONResponseFromGSON>,
                t: Throwable) {
                Toast.makeText(activity, "에러 발생 : ${t.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<OpenWeatherAPIJSONResponseFromGSON>,
                response: Response<OpenWeatherAPIJSONResponseFromGSON>
            ) {
                val data : OpenWeatherAPIJSONResponseFromGSON =
                    response.body() as OpenWeatherAPIJSONResponseFromGSON

                val temp = data.main.get("temp")
                tempText.text = temp

                val id = data.weather[0].get("id")
                if(id != null) {
                    statusText.text = when {
                        id.startsWith("2") -> {
                            weatherImage.setImageResource(R.drawable.flash)
                            "천둥, 번개"
                        }
                        id.startsWith("3") -> {
                            weatherImage.setImageResource(R.drawable.rain)
                            "이슬비"
                        }
                        id.startsWith("5") -> {
                            weatherImage.setImageResource(R.drawable.rain)
                            "비"
                        }
                        id.startsWith("6") -> {
                            weatherImage.setImageResource(R.drawable.snow)
                            "눈"
                        }
                        id.startsWith("7") -> {
                            weatherImage.setImageResource(R.drawable.cloudy)
                            "흐림"
                        }
                        id.equals("800") -> {
                            weatherImage.setImageResource(R.drawable.sun)
                            "화창"
                        }
                        id.startsWith("8") -> {
                            weatherImage.setImageResource(R.drawable.cloud)
                            "구름 낌"
                        }
                        else -> "알 수 없음"
                    }
                }
            }
        })

        /*
        val url = "http://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=871e998f753c9217f41114a8c5a5f0f6&units=metric"

        val apiCallback = object : APICall.APICallback {
            override fun onComplete(result: String) {
                // Log.d("mytag", result)
                // 역직렬화
                val mapper = jacksonObjectMapper()
                val data = mapper?.readValue<OpenWeatherAPIJSONResponse>(result)

                val temp = data.main.get("temp")
                tempText.text = temp

                val id = data.weather[0].get("id")
                if(id != null) {
                    statusText.text = when {
                        id.startsWith("2") -> {
                            weatherImage.setImageResource(R.drawable.flash)
                            "천둥, 번개"
                        }
                        id.startsWith("3") -> {
                            weatherImage.setImageResource(R.drawable.rain)
                            "이슬비"
                        }
                        id.startsWith("5") -> {
                            weatherImage.setImageResource(R.drawable.rain)
                            "비"
                        }
                        id.startsWith("6") -> {
                            weatherImage.setImageResource(R.drawable.snow)
                            "눈"
                        }
                        id.startsWith("7") -> {
                            weatherImage.setImageResource(R.drawable.cloudy)
                            "흐림"
                        }
                        id.equals("800") -> {
                            weatherImage.setImageResource(R.drawable.sun)
                            "화창"
                        }
                        id.startsWith("8") -> {
                            weatherImage.setImageResource(R.drawable.cloud)
                            "구름 낌"
                        }
                        else -> "알 수 없음"
                    }
                }
            }
        }
        val call = APICall(apiCallback)
        call.execute(URL(url))
        */
    }

    companion object {
        fun newInstance(lat: Double, lon: Double) : WeatherPageFragment {
            val fragment = WeatherPageFragment()

            val args = Bundle()
            args.putDouble("lat", lat)
            args.putDouble("lon", lon)
            fragment.arguments = args

            return fragment
        }

        /*
        fun newInstance(status: String, temp: Double)
            : WeatherPageFragment{
            val fragment = WeatherPageFragment()

            // 번들 객체 생성하여 프래그먼트로 전달할 데이터 저장
            val args = Bundle()
            args.putString("status", status)
            args.putDouble("temp", temp)
            args.putInt("res_id", R.drawable.sun)

            fragment.arguments = args

            return fragment
        }
        */
    }

    fun startAnimation() {
        val fadeIn : Animation = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
        weatherImage.startAnimation(fadeIn)
    }

}














