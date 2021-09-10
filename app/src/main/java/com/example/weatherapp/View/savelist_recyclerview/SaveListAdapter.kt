package com.example.weatherapp.View.savelist_recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.example.weatherapp.Model.cache.RealmLocationModel
import com.example.weatherapp.R

class SaveListAdapter(diffUtli: MyInterfaceForListAdapter): ListAdapter<String, SaveLocationHolder>(diffUtli) {

    override fun onBindViewHolder(holder: SaveLocationHolder, position: Int) {
        val currentModelInList = getItem(position)
        holder.bind(currentModelInList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveLocationHolder {
        val viewForHolder = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        val problemHolderInstance = SaveLocationHolder(viewForHolder)
        return problemHolderInstance
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_drawer
    }

}

class MyInterfaceForListAdapter: DiffUtil.ItemCallback<String>(){
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        Log.d("AdapterTag", "Сравнение значений $oldItem и $newItem. Итог - ${oldItem == newItem}")
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem.equals(newItem)
    }

}