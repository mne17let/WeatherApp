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
    val searchLiveData = MutableLiveData<List<String>>()

    fun getWeather(text: String){
        viewModelScope.launch {
            val result: RepositoryResult = repository.getWeatherFromRepository(text)

            Log.d(TAG_VIEWMODEL, "Во вьюмодель получены данные: $result")

            when(result){
                is RepositoryResult.CloudSuccessRepositoryResult ->{
                    Log.d(TAG_VIEWMODEL, "Во вьюмодели сохранено: ${result.isSaved}")
                    cloudLiveData.value = LiveDataState.Weather(result)
                }
                is RepositoryResult.CloudErrorRepositoryResult ->
                    cloudLiveData.value = LiveDataState.Error(result)
            }
        }
    }

    fun saveOrDeleteCurrentLocation(){
        viewModelScope.launch {
            val result: RepositoryResult = repository.addOrRemoveLocation()

            if(result is RepositoryResult.CacheRepositoryResult){
                Log.d(TAG_VIEWMODEL, "Во вьюмодели список после удаления или добавления местоположения. До сортировки: ${result.newList}")

                val sorted = result.newList.sorted()

                Log.d(TAG_VIEWMODEL, "Во вьюмодели список после удаления или добавления местоположения. После сортировки: $sorted")

                cacheLiveData.value = CacheAnswer(result.message, result.isError)
                savedListLiveData.value = sorted
            }
        }
    }

    fun getSaveList(){
        viewModelScope.launch {
            val answerFromRepository = repository.getLocationsList()

            Log.d(TAG_VIEWMODEL, "Во вьюмодели получен список во время загрузки приложения. До сортировки: ${answerFromRepository.data}")

            val listFromRepository = answerFromRepository.data
            val sorted = listFromRepository.sorted()

            Log.d(TAG_VIEWMODEL, "Во вьюмодели список во время загрузки приложения. После сортировки: ${answerFromRepository.data}")

            savedListLiveData.value = sorted
        }
    }

    fun searchLocations(text: String){
        viewModelScope.launch {
            val result = repository.repositorySearch(text)

            val mutableList: MutableList<String> = mutableListOf()

            Log.d(TAG_VIEWMODEL, "Во вьюмодели выполнен поиск с текстом: $text")
            Log.d(TAG_VIEWMODEL, "Во вьюмодели получен результат: $result")

            if(result.isNotEmpty()){
                searchLiveData.value = result
            } else{
                searchLiveData.value = emptyList()
            }

        }
    }

    sealed class LiveDataState{
        data class Weather(val currentWeather: RepositoryResult.CloudSuccessRepositoryResult): LiveDataState()
        data class Error(val cloudError: RepositoryResult.CloudErrorRepositoryResult): LiveDataState()
    }

    data class CacheAnswer(val message: String, val isError: Boolean)

}