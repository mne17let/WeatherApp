package com.example.weatherapp

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.weatherapp.Model.Repository
import com.example.weatherapp.Model.api.WeatherApi
import com.example.weatherapp.ViewModel.WeatherViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApplication: Application() {

    lateinit var weatherViewModel: WeatherViewModel
    private lateinit var weatherApi: WeatherApi
    private lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()

        val retrofit: Retrofit = Retrofit
            .Builder()
            .baseUrl("https://api.weatherapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApi = retrofit.create(WeatherApi::class.java)

        repository = Repository(weatherApi)

        weatherViewModel = WeatherViewModel(repository)
    }



}