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

    private lateinit var textViewDate: TextView
    private lateinit var textViewMaxTemp: TextView
    private lateinit var textViewMinTemp: TextView
    private lateinit var textViewAvgTemp: TextView
    private lateinit var textViewDescription: TextView
    private lateinit var imageViewIcon: ImageView

    fun bind(data: ForecastDayModel){
        textViewDate = itemView.findViewById(R.id.id_tv_rv_date)
        textViewMaxTemp = itemView.findViewById(R.id.id_tv_max_temp)
        textViewMinTemp = itemView.findViewById(R.id.id_tv_rv_min_temp)
        textViewAvgTemp = itemView.findViewById(R.id.id_tv_rv_avg_temp)
        textViewDescription = itemView.findViewById(R.id.id_tv_rv_description)
        imageViewIcon = itemView.findViewById(R.id.id_iv_rv_weather_icon)

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