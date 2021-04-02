package com.example.weatherdustchecker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager

class WeatherDustMainActivity : AppCompatActivity() {
    private lateinit var mPager: ViewPager
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    private val PERMISSION_REQUEST_CODE : Int = 1
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_dust_main_activity)

        supportActionBar?.hide()

        mPager = findViewById<ViewPager>(R.id.pager)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                lat = location.latitude
                lon = location.longitude
                Log.d("mytag", "${lat}, ${lon}")

                locationManager.removeUpdates(this)

                val pagerAdapter = MyPagerAdpater(supportFragmentManager)
                mPager.adapter = pagerAdapter

                mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {}
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                    override fun onPageSelected(position: Int) {
                        if(position == 0) {
                            // Toast.makeText(this@WeatherDustMainActivity, "날씨 페이지입니다.", Toast.LENGTH_SHORT).show()
                            val fragment = mPager.adapter?.instantiateItem(
                                mPager,
                                position) as WeatherPageFragment

                            fragment.startAnimation()
                        } else if(position == 1) {
                            // Toast.makeText(this@WeatherDustMainActivity, "미세먼지 페이지입니다.", Toast.LENGTH_SHORT).show()
                            val fragment = mPager.adapter?.instantiateItem(
                                mPager,
                                position) as DustPageFragment

                            fragment.startAnimation()
                        }
                    }
                })
            }
        }

        if(ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        // 권한 허용 결과에 따라서 수행할 코드 작성
        var allPermissionsGranted = true
        for(result in grantResults) {
            allPermissionsGranted = (result == PackageManager.PERMISSION_GRANTED)
            if(!allPermissionsGranted) break
        }
        if(allPermissionsGranted) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        } else {
            Toast.makeText(applicationContext, "위치 정보 제공 동의가 필요합니다.", Toast.LENGTH_SHORT).show()
            this.finish()
        }
    }

    inner class MyPagerAdpater(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when(position) {
                0 -> WeatherPageFragment.newInstance(lat, lon)
                1 -> DustPageFragment.newInstance(lat, lon)
                else -> {
                    throw Exception("페이지가 존재하지 않음.")
                }
            }
        }
        override fun getCount(): Int = 2
    }

}














