package com.example.weatherapp.View.forecast_recyclerview

import android.accounts.AuthenticatorDescription
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.Model.api.weatherModels.forecast.ForecastDayModel
import com.example.weatherapp.Model.api.weatherModels.forecast.ForecastModel
import com.example.weatherapp.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class ForecastViewHolder(forecastView: View): RecyclerView.ViewHolder(forecastView) {

    private val textViewDate: TextView = itemView.findViewById(R.id.id_tv_rv_date)
    private val textViewMaxTemp: TextView = itemView.findViewById(R.id.id_tv_max_temp)
    private val textViewMinTemp: TextView = itemView.findViewById(R.id.id_tv_rv_min_temp)
    private val textViewAvgTemp: TextView = itemView.findViewById(R.id.id_tv_rv_avg_temp)
    private val textViewDescription: TextView = itemView.findViewById(R.id.id_tv_rv_description)
    private val imageViewIcon: ImageView = itemView.findViewById(R.id.id_iv_rv_weather_icon)

    fun bind(data: ForecastDayModel){
        setText(data)
        setImage(data)
    }

    fun setText(data: ForecastDayModel){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val  formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = LocalDate.parse(data.date, formatter)

            val formatterFromDate = DateTimeFormatter.ofPattern("d MMMM yyy, EEEE")
            val formattedDate = date.format(formatterFromDate)
            textViewDate.text = formattedDate
        } else{
            textViewDate.text = data.date
        }

        val roundMax = data.day.maxtemp_c.roundToInt()
        val roundMin = data.day.mintemp_c.roundToInt()
        val roundAvg = data.day.avgtemp_c.roundToInt()


        textViewMaxTemp.text = if(roundMax > 0) "↑ +${roundMax}" else "↑ ${roundMax}"
        textViewMinTemp.text = if(roundMin > 0) "↓ +${roundMin}" else "↓ ${roundMin}"
        textViewAvgTemp.text = if(roundAvg > 0) "+${roundAvg}" else "${roundAvg}"
        textViewDescription.text = data.day.one_day_forecast_condition.weather_text


    }

    fun setImage(data: ForecastDayModel){
        val stringUrl = data.day.one_day_forecast_condition.icon_url
        val url = "https:$stringUrl"

        Glide.with(imageViewIcon).load(url).into(imageViewIcon)
    }
}