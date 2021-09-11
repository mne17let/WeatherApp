package com.example.weatherapp.View.savelist_recyclerview

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R

class SavedLocationsAdapter(private val necessaryContext: Context,
                            private val listener: SaveClickListener)
    : RecyclerView.Adapter<SaveLocationHolder>() {

    private val TAG_SAVED_LOCATIONS_ADAPTER = "MySaveListAdapter"

    private var viewsList: MutableList<View> = mutableListOf()
    private var locationsList = emptyList<String>()
    private var isClicked = false
    private var itemClicked: String? = null


    fun setClicked(isClicked: Boolean, itemClicked: String?){
        this.isClicked = isClicked
        this.itemClicked = itemClicked

        Log.d(TAG_SAVED_LOCATIONS_ADAPTER, "В адаптер установлены: $isClicked и $itemClicked")

        notifyDataSetChanged()
    }

    fun setList(list: List<String>) {
        locationsList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveLocationHolder {
        val viewForHolder = LayoutInflater.from(parent.context).inflate(R.layout.item_drawer, parent, false)
        val holder = SaveLocationHolder(viewForHolder)
        return holder
    }

    override fun onBindViewHolder(holder: SaveLocationHolder, position: Int) {
        holder.bind(locationsList[position])

        if(!viewsList.contains(holder.newView)){
            viewsList.add(holder.newView)
        }

        if(isClicked == true){
            if(locationsList[position] == itemClicked){
                Log.d(TAG_SAVED_LOCATIONS_ADAPTER, "Сравнение: ${locationsList[position]} и $itemClicked")
                holder.newView.setBackgroundColor(necessaryContext.resources.getColor(R.color.selected_saved_item))
                Log.d(TAG_SAVED_LOCATIONS_ADAPTER, "Байнд нажатого: ${locationsList[position]}")
            } else{
                Log.d(TAG_SAVED_LOCATIONS_ADAPTER, "Сравнение: ${locationsList[position]} и $itemClicked")
                holder.newView.setBackgroundColor(necessaryContext.resources.getColor(R.color.white))
                Log.d(TAG_SAVED_LOCATIONS_ADAPTER, "Байнд ненажатого: ${locationsList[position]}")
            }
        }

        holder.newView.setOnClickListener{
            for (i in viewsList){
                i.setBackgroundColor(necessaryContext.resources.getColor(R.color.white))
            }
            it.setBackgroundColor(necessaryContext.resources.getColor(R.color.selected_saved_item))

            Log.d(TAG_SAVED_LOCATIONS_ADAPTER, "Нажат холдер с итемом: ${locationsList[position]}")

            listener.onClick(locationsList[position])
        }
    }

    override fun getItemCount(): Int {
        return locationsList.size
    }

    interface SaveClickListener {
        fun onClick(string: String)
    }
}