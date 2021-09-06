package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.Model.Repository
import com.example.weatherapp.Model.api.WeatherApi
import com.example.weatherapp.ViewModel.SearchViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApplication: Application() {

    lateinit var searchViewModel: SearchViewModel
    private lateinit var weatherApi: WeatherApi
    private lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()

        val retrofit: Retrofit = Retrofit
            .Builder()
            .baseUrl("https://vk.com/feed/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApi = retrofit.create(WeatherApi::class.java)

        repository = Repository(weatherApi)

        searchViewModel = SearchViewModel(repository)
    }
}