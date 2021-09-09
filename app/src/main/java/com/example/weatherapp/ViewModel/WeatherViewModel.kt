package com.example.weatherapp.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Model.Repository
import com.example.weatherapp.Model.api.WeatherApi
import kotlinx.coroutines.launch

class WeatherViewModel(): ViewModel() {

    private val TAG_VIEWMODEL = "MyViewModel"

    private var repository: Repository = Repository()

    val mutableLiveData = MutableLiveData<LiveDataState>()

    fun search(text: String){
        viewModelScope.launch {
            val result: Repository.RepositoryResult = repository.getWeatherFromRepository(text)

            Log.d(TAG_VIEWMODEL, "Во вьюмодель получены данные: $result")

            when(result){
                is Repository.RepositoryResult.SuccessRepositoryResult ->
                    mutableLiveData.value = LiveDataState.Weather(result)
                is Repository.RepositoryResult.ErrorRepositoryResult ->
                    mutableLiveData.value = LiveDataState.Error(result)
            }
        }
    }

    sealed class LiveDataState{
        data class Weather(val currentWeather: Repository.RepositoryResult.SuccessRepositoryResult): LiveDataState()
        data class Error(val error: Repository.RepositoryResult.ErrorRepositoryResult): LiveDataState()
    }

}