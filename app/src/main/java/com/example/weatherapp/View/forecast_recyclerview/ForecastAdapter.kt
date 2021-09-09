package com.example.weatherapp.View.forecast_recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R

class ForecastAdapter: RecyclerView.Adapter<ForecastViewHolder>() {

    private var itemsList = emptyList<Any>()

    fun setList(list: List<Any>){
        itemsList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_forecast_recyclerview, parent, false)
        val newViewHolder = ForecastViewHolder(view)
        return newViewHolder
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}