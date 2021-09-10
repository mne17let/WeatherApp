package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.Model.Repository
import com.example.weatherapp.Model.api.WeatherApi
import com.example.weatherapp.Model.cache.CacheDataSource
import com.example.weatherapp.Model.cloud.Cloud
import com.example.weatherapp.ViewModel.WeatherViewModel
import io.realm.Realm
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApplication: Application() {

    lateinit var viewModel: WeatherViewModel
    private lateinit var repository: Repository
    private lateinit var realm: Realm
    private lateinit var cloud: Cloud
    private lateinit var cacheDataSource: CacheDataSource
    private lateinit var retrofit: Retrofit
    private lateinit var weatherApi: WeatherApi

    override fun onCreate() {
        super.onCreate()
        setRealm()
        setCache()
        setRetrofit()
        setCloud()
        setRepository()
        setViewModel()
    }

    fun setRealm(){
        Realm.init(this)
        realm = Realm.getDefaultInstance()
    }

    fun setCache(){
        cacheDataSource = CacheDataSource()
    }

    fun setRetrofit(){
        retrofit = Retrofit
            .Builder()
            .baseUrl("https://api.weatherapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApi = retrofit.create(WeatherApi::class.java)
    }

    fun setCloud(){
        cloud = Cloud(weatherApi, retrofit)
    }

    fun setRepository(){
        repository = Repository(cloud, cacheDataSource)
    }

    fun setViewModel(){
        viewModel = WeatherViewModel(repository)
    }

}