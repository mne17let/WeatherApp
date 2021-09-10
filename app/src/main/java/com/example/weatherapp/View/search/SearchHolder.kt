package com.example.weatherapp.View.search

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R

class SearchHolder(newView: View): RecyclerView.ViewHolder(newView) {

    private val textView: TextView = itemView.findViewById(R.id.id_search_result_text)

    fun bind(text: String){
        textView.text = text
    }
}