package com.example.weatherdustchecker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

class DustPageFragment : Fragment() {
    @JsonDeserialize(using=DustCheckerResponseDeserializer::class)
    data class DustCheckResponse(
         val pm10: Int?,
         val pm25: Int?,
         val pm10Status : String,
         val pm25Status : String)

    class DustCheckerResponseDeserializer :
        StdDeserializer<DustCheckResponse>(DustCheckResponse::class.java) {
        private val checkCategory = { aqi : Int? -> when(aqi) {
            null -> "알 수 없음"
            in (0 .. 100) -> "좋음"
            in (101 .. 200) -> "보통"
            in (201 .. 300) -> "나쁨"
            else -> "매우 나쁨"
        }}

        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): DustCheckResponse {
            var node : JsonNode? = p?.codec?.readTree<JsonNode>(p)

            var dataNode : JsonNode? = node?.get("data")
            var iaqiNode = dataNode?.get("iaqi")
            var pm10Node = iaqiNode?.get("pm10")
            var pm25Node = iaqiNode?.get("pm25")
            var pm10 = pm10Node?.get("v")?.asInt()
            var pm25 = pm25Node?.get("v")?.asInt()

            var pm10Status = checkCategory(pm10)
            var pm25Status = checkCategory(pm25)

            return DustCheckResponse(pm10, pm25, pm10Status, pm25Status)
        }
    }

    lateinit var statusImage : ImageView
    lateinit var pm25StatusText : TextView
    lateinit var pm25IntensityText : TextView
    lateinit var pm10StatusText : TextView
    lateinit var pm10IntensityText : TextView

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?)
            : View? {
        val view = inflater.inflate(
            R.layout.dust_page_fragment,
            container,
            false)

        statusImage = view.findViewById<ImageView>(R.id.dust_status_icon)
        pm25StatusText = view.findViewById<TextView>(R.id.dust_pm25_status_text)
        pm25IntensityText = view.findViewById<TextView>(R.id.dust_pm25_intensity_text)
        pm10StatusText = view.findViewById<TextView>(R.id.dust_pm10_status_text)
        pm10IntensityText = view.findViewById<TextView>(R.id.dust_pm10_intensity_text)

        return view
    }

    companion object {
        fun newInstance(lat: Double, lon: Double) : DustPageFragment {
            val fragment = DustPageFragment()

            val args = Bundle()
            args.putDouble("lat", lat)
            args.putDouble("lon", lon)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lat = arguments!!.getDouble("lat")
        val lon = arguments!!.getDouble("lon")

        val retrofit = Retrofit.Builder()
            .baseUrl("http://api.waqi.info")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().registerTypeAdapter(
                        DustCheckResponseFromGSON::class.java,
                        DustCheckerResponseDeserializerGSON()
                    ).create()
                ))
            .build()

        val apiService = retrofit.create(DustCheckAPIService::class.java)
        val apiCallForData =
            apiService.getDustStatusInfo(lat, lon, "65b74595bd8ed6def7f63b4a9a8655312fd602d1")

        apiCallForData.enqueue(object : Callback<DustCheckResponseFromGSON> {
            override fun onFailure(call: Call<DustCheckResponseFromGSON>, t: Throwable) {
                Toast.makeText(activity, "에러 발생 : ${t.message}", Toast.LENGTH_SHORT).show()
            }

            // (4)
            override fun onResponse(call: Call<DustCheckResponseFromGSON>, response: Response<DustCheckResponseFromGSON>) {
                val data = response.body()

                if(data != null) {
                    // (1)
                    statusImage.setImageResource(when(data.pm25Status) {
                        "좋음" -> R.drawable.good
                        "보통" -> R.drawable.normal
                        "나쁨" -> R.drawable.bad
                        else -> R.drawable.very_bad
                    })

                    pm25IntensityText.text = data.pm25?.toString() ?: "알 수 없음"
                    pm10IntensityText.text = data.pm10?.toString() ?: "알 수 없음"


                    // (2)
                    pm25StatusText.text = "${data.pm25Status} (초미세먼지)"
                    pm10StatusText.text = "${data.pm10Status} (미세먼지)"
                }
            }
        })

        /*
        val url = "http://api.waqi.info/feed/geo:${lat};${lon}/?token=65b74595bd8ed6def7f63b4a9a8655312fd602d1"

        val apiCallback = object : APICall.APICallback {
            override fun onComplete(result: String) {
                Log.d("mytag", result)
                val mapper = jacksonObjectMapper()
                val data = mapper?.readValue<DustCheckResponse>(result)

                statusImage.setImageResource(when(data.pm25Status) {
                    "좋음" -> R.drawable.good
                    "보통" -> R.drawable.normal
                    "나쁨" -> R.drawable.bad
                    else -> R.drawable.very_bad
                })

                pm25IntensityText.text = data.pm25?.toString() ?: "알 수 없음"
                pm10IntensityText.text = data.pm10?.toString() ?: "알 수 없음"

                pm25StatusText.text = "${data.pm25Status} (초미세먼지)"
                pm10StatusText.text = "${data.pm10Status} (미세먼지)"
            }
        }
        val call = APICall(apiCallback)
        call.execute(URL(url))
        */
    }

    fun startAnimation() {
        val fadeIn = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
        statusImage.startAnimation(fadeIn)
    }


}




