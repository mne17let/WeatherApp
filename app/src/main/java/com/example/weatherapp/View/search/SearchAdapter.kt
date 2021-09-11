package com.example.weatherapp.View.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import kotlinx.coroutines.delay

class SearchAdapter(private val listener: ClickListener): RecyclerView.Adapter<SearchHolder>() {

    private var itemsList = emptyList<String>()

    fun setList(list: List<String>){
        itemsList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_recyclerview, parent, false)

        return SearchHolder(view)
    }

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        holder.bind(itemsList[position], listener)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    interface ClickListener{
        fun onClick(string: String)
    }
}