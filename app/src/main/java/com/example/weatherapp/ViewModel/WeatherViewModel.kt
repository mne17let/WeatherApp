package com.example.weatherapp.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Model.Repository
import com.example.weatherapp.Model.RepositoryResult
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: Repository): ViewModel() {

    private val TAG_VIEWMODEL = "MyViewModel"

    //private val repository: Repository = Repository()

    val cloudLiveData = MutableLiveData<LiveDataState>()
    val cacheLiveData = MutableLiveData<CacheAnswer>()
    val savedListLiveData = MutableLiveData<List<String>>()

    fun search(text: String){
        viewModelScope.launch {
            val result: RepositoryResult = repository.getWeatherFromRepository(text)

            Log.d(TAG_VIEWMODEL, "Во вьюмодель получены данные: $result")

            when(result){
                is RepositoryResult.CloudSuccessRepositoryResult ->
                    cloudLiveData.value = LiveDataState.Weather(result)
                is RepositoryResult.CloudErrorRepositoryResult ->
                    cloudLiveData.value = LiveDataState.Error(result)
            }
        }
    }

    fun saveOrDeleteCurrentLocation(){
        viewModelScope.launch {
            val result: RepositoryResult = repository.addOrRemoveLocation()

            if(result is RepositoryResult.CacheRepositoryResult){
                cacheLiveData.value = CacheAnswer(result.message, result.isError)
                savedListLiveData.value = result.newList
            }
        }
    }

    fun getSaveList(){
        viewModelScope.launch {
            val answerFromRepository = repository.getLocationsList()

            val listFromRepository = answerFromRepository.data
            val sorted = listFromRepository.sorted()

            savedListLiveData.value = sorted
        }
    }

    sealed class LiveDataState{
        data class Weather(val currentWeather: RepositoryResult.CloudSuccessRepositoryResult): LiveDataState()
        data class Error(val cloudError: RepositoryResult.CloudErrorRepositoryResult): LiveDataState()
    }

    data class CacheAnswer(val message: String, val isError: Boolean)

}