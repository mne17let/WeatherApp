package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.weatherapp.ViewModel.SearchViewModel

class MainActivity: AppCompatActivity() {

    private val TAG_ACTIVITY = "MyMainActivity"

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var textViewCityName: TextView
    private lateinit var textViewCurrentTemperature: TextView
    private lateinit var imageViewWeatherIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setViewModel()
        init()
        setLiveData()
        searchViewModel.search("Певек")
    }

    fun setViewModel(){
        val myApplication: MyApplication = application as MyApplication
        searchViewModel = myApplication.searchViewModel
    }

    fun init(){
        textViewCityName = findViewById(R.id.id_textview_cityname)
        textViewCurrentTemperature = findViewById(R.id.id_textview_temperature)
        imageViewWeatherIcon = findViewById(R.id.id_imageview_weather_icon)
    }

    fun setLiveData(){
        searchViewModel.mutableLiveData.observe(this){
            Log.d(TAG_ACTIVITY, "В активити получен результат: $it")

            textViewCityName.text = it[0].toString()
            textViewCurrentTemperature.text = it[1].toString()
            val stringUrl = it[2].toString()
            val url = "http:$stringUrl"
            Log.d(TAG_ACTIVITY, "url = $url")
            Glide.with(imageViewWeatherIcon).load(url).into(imageViewWeatherIcon)
            //imageViewWeatherIcon.setBackgroundColor(resources.getColor(R.color.black))
        }
    }
}