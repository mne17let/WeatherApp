package com.example.weatherapp.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Model.Repository
import com.example.weatherapp.Model.api.WeatherApi
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: Repository): ViewModel() {

    private val TAG_VIEWMODEL = "MyViewModel"

    val mutableLiveData = MutableLiveData<List<Any>>()

    fun search(cityName: String){
        viewModelScope.launch {
            val result = repository.getCurrentWeatherFromRepository(cityName)

            Log.d(TAG_VIEWMODEL, "Во вьюмодель получены данные: $result")

            mutableLiveData.value = result
        }
    }

}