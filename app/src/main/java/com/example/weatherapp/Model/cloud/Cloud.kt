package com.example.weatherapp.Model.cloud

import android.util.Log
import com.example.weatherapp.Model.api.WeatherApi
import com.example.weatherapp.Model.api.currentWeather.ResponseForecast
import com.example.weatherapp.Model.api.currentWeather.ServerErrorResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class Cloud {

    private val TAG_CLOUD = "MyCloud"

    private val retrofit: Retrofit = Retrofit
        .Builder()
        .baseUrl("https://api.weatherapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherApi: WeatherApi = retrofit.create(WeatherApi::class.java)

    suspend fun getWeather(searchString: String): CloudAnswer {

        val cloudAnswer: CloudAnswer

        val result: Response<ResponseForecast>

        withContext(Dispatchers.IO) {
            @Suppress("BlockingMethodInNonBlockingContext")
                result = weatherApi.getForecast(searchString).execute()
        }

        if (!result.isSuccessful) {
            val errorBody = result.errorBody()
            if(errorBody != null){
                cloudAnswer = getResultErrorAnswer(errorBody)
            } else{
                cloudAnswer = CloudAnswer.Error(CloudError.NO_TYPE_ERROR,
                    "Неизвестная ошибка. Тело ответа с ошибкой пустое")
            }
        } else {
            val body = result.body()
            if(body != null){
                cloudAnswer = CloudAnswer.Success(body)
            } else{
                cloudAnswer = CloudAnswer.Error(CloudError.NO_TYPE_ERROR,
                    "Неизвестная ошибка. Запрос успешный, но тело ответа пустое")
            }
        }

        val code: Int = result.code()
        val body: ResponseForecast? = result.body()

        Log.d(TAG_CLOUD, "Код ответа: ${code}")
        Log.d(TAG_CLOUD, "Получен ответ: ${result}")
        Log.d(TAG_CLOUD, "Получен ответ: ${result.body()?.forecast}")
        Log.d(TAG_CLOUD, "Размер массива: ${result.body()?.forecast?.forecastday?.size}")

        /*cloudAnswer = CloudAnswer.Error(CloudError.NO_TYPE_ERROR,
            "Неизвестная ошибка. Не удалось выполнить запрос")*/

        return cloudAnswer
    }

    private fun getResultErrorAnswer(errorBody: ResponseBody): CloudAnswer {
        var finalError: CloudAnswer

        val converter: Converter<ResponseBody, ServerErrorResponseModel> =
            retrofit.responseBodyConverter(
                ServerErrorResponseModel::class.java, arrayOf(object : kotlin.Annotation {})
            )

        try {
            val serverErrorResponse = converter.convert(errorBody)

            val error = serverErrorResponse?.serverErrorModel

            if (error != null) {
                val errorCode = error.code
                val errorMessage = error.message

                when (errorCode) {
                    1002 -> finalError = CloudAnswer.Error(CloudError.API_KEY_NOT_PROVIDED, errorMessage)
                    1003 -> finalError = CloudAnswer.Error(CloudError.WRONG_Q, errorMessage)
                    1005 -> finalError = CloudAnswer.Error(CloudError.API_REQUEST_IS_INVALID, errorMessage)
                    1006 -> finalError = CloudAnswer.Error(CloudError.NO_LOCATION_FOUND, errorMessage)
                    2006 -> finalError =
                        CloudAnswer.Error(CloudError.API_KEY_PROVIDED_IS_INVALID, errorMessage)
                    2007 -> finalError = CloudAnswer.Error(
                        CloudError.API_KEY_HAS_EXCEEDED_CALLS_PER_MONTH_QUOTA,
                        errorMessage
                    )
                    2008 -> finalError =
                        CloudAnswer.Error(CloudError.API_KEY_HAS_BEEN_DISABLED, errorMessage)
                    9999 -> finalError = CloudAnswer.Error(CloudError.INTERNAL_ERROR, errorMessage)
                    else -> finalError = CloudAnswer.Error(CloudError.NO_TYPE_ERROR, "Неизвестная ошибка. Неизвестный код ошибки")
                }
            } else{
                finalError = CloudAnswer.Error(CloudError.NO_TYPE_ERROR, "Неизвестная ошибка, тело ответа пустое")
            }
        } catch (e: Exception) {

            Log.d(TAG_CLOUD, "Выполнен блок catch")

            finalError = CloudAnswer.Error(CloudError.NO_TYPE_ERROR, "Неизвестная ошибка из блока catch")
            e.printStackTrace()
        }

        return finalError
    }

    sealed class CloudAnswer {
        data class Error(val errorType: CloudError, val message: String) : CloudAnswer()
        data class Success(val data: ResponseForecast) : CloudAnswer()
    }

    enum class CloudError {
        API_KEY_NOT_PROVIDED,
        WRONG_Q,
        API_REQUEST_IS_INVALID,
        NO_LOCATION_FOUND,
        API_KEY_PROVIDED_IS_INVALID,
        API_KEY_HAS_EXCEEDED_CALLS_PER_MONTH_QUOTA,
        API_KEY_HAS_BEEN_DISABLED,
        INTERNAL_ERROR,
        NO_TYPE_ERROR
    }
}