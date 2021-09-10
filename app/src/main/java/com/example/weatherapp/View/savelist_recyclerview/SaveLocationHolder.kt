package com.example.weatherapp.View.savelist_recyclerview

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Model.cache.RealmLocationModel
import com.example.weatherapp.R

class SaveLocationHolder(newView: View): RecyclerView.ViewHolder(newView) {

    private val textViewCityName: TextView = itemView.findViewById(R.id.id_textview_item_save_location)

    fun bind(modelLocation: String){
        textViewCityName.text = modelLocation
    }
}