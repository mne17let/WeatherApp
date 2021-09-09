package com.example.weatherapp.View.forecast_recyclerview

import android.accounts.AuthenticatorDescription
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R

class ForecastViewHolder(forecastView: View): RecyclerView.ViewHolder(forecastView) {

    private lateinit var textViewDate: TextView
    private lateinit var textViewMaxTemp: TextView
    private lateinit var textViewMinTemp: TextView
    private lateinit var textViewAvgTemp: TextView
    private lateinit var textViewDescription: TextView
    private lateinit var imageViewIcon: ImageView

    fun bind(){
        textViewDate = itemView.findViewById(R.id.id_tv_rv_date)
        textViewMaxTemp = itemView.findViewById(R.id.id_tv_max_temp)
        textViewMinTemp = itemView.findViewById(R.id.id_tv_rv_min_temp)
        textViewAvgTemp = itemView.findViewById(R.id.id_tv_rv_avg_temp)
        textViewDescription = itemView.findViewById(R.id.id_tv_rv_description)
        imageViewIcon = itemView.findViewById(R.id.id_iv_rv_weather_icon)
    }
}