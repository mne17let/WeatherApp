package com.example.weatherapp.View.savelist_recyclerview

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Model.cache.RealmLocationModel
import com.example.weatherapp.R
import com.example.weatherapp.View.search.SearchAdapter

class SaveLocationHolder(val newView: View): RecyclerView.ViewHolder(newView) {

    private val textViewCityName: TextView = itemView.findViewById(R.id.id_textview_item_save_location)

    fun bind(stringLocation: String){
        textViewCityName.text = stringLocation
    }
}