package com.example.weatherapp.View.savelist_recyclerview

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Model.cache.RealmLocationModel
import com.example.weatherapp.R

class SaveLocationHolder(newView: View): RecyclerView.ViewHolder(newView) {

    private lateinit var textViewCityName: TextView

    fun bind(modelLocation: String){
        textViewCityName = itemView.findViewById(R.id.id_textview_item_save_location)
        textViewCityName.text = modelLocation
    }
}