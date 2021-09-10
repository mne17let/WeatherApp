package com.example.weatherapp.View.forecast_recyclerview

import android.accounts.AuthenticatorDescription
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.Model.api.weatherModels.forecast.ForecastDayModel
import com.example.weatherapp.Model.api.weatherModels.forecast.ForecastModel
import com.example.weatherapp.R

class ForecastViewHolder(forecastView: View): RecyclerView.ViewHolder(forecastView) {

    private val textViewDate: TextView = itemView.findViewById(R.id.id_tv_rv_date)
    private val textViewMaxTemp: TextView = itemView.findViewById(R.id.id_tv_max_temp)
    private val textViewMinTemp: TextView = itemView.findViewById(R.id.id_tv_rv_min_temp)
    private val textViewAvgTemp: TextView = itemView.findViewById(R.id.id_tv_rv_avg_temp)
    private val textViewDescription: TextView = itemView.findViewById(R.id.id_tv_rv_description)
    private val imageViewIcon: ImageView = itemView.findViewById(R.id.id_iv_rv_weather_icon)

    fun bind(data: ForecastDayModel){
        setData(data)
    }

    fun setData(data: ForecastDayModel){
        textViewDate.text = data.date
        textViewMaxTemp.text = data.day.maxtemp_c.toString()
        textViewMinTemp.text = data.day.mintemp_c.toString()
        textViewAvgTemp.text = data.day.avgtemp_c.toString()
        textViewDescription.text = data.day.one_day_forecast_condition.weather_text

        val stringUrl = data.day.one_day_forecast_condition.icon_url
        val url = "https:$stringUrl"

        Glide.with(imageViewIcon).load(url).into(imageViewIcon)
    }
}