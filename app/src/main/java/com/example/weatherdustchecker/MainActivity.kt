package com.example.weatherdustchecker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()

        /*
        transaction.add(R.id.fragment_container,
            WeatherPageFragment.newInstance(37.58, 126.98))
        */
        transaction.add(R.id.fragment_container,
            DustPageFragment.newInstance(37.58, 126.98))

        transaction.commit()
    }
}
