package com.example.weatherapp.View.savelist_recyclerview

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.weatherapp.R

class SaveListAdapter(diffUtli: MyInterfaceForListAdapter, private val requireContext: Context,
    private val listener: SaveClickListener): ListAdapter<String, SaveLocationHolder>(diffUtli) {

    private val TAG_SAVE_LIST_ADAPTER = "MySaveListAdapter"

    private var holdersViewsList: MutableList<View> = mutableListOf()


    override fun onBindViewHolder(holder: SaveLocationHolder, position: Int) {
        val currentStringLocationInList = getItem(position)
        holder.bind(currentStringLocationInList)

        Log.d(TAG_SAVE_LIST_ADAPTER, "В адаптере вызван холдер с позицией: $position")
        Log.d(TAG_SAVE_LIST_ADAPTER, "В адаптере список: $$currentList")
        Log.d(TAG_SAVE_LIST_ADAPTER, "В адаптере размер списка: ${currentList.size}")
        Log.d(TAG_SAVE_LIST_ADAPTER, "В адаптере в методе bind вызван: $currentStringLocationInList")

        if(!holdersViewsList.contains(holder.newView)){
            holdersViewsList.add(holder.newView)
        }

        holder.newView.setOnClickListener{
            for (i in holdersViewsList){
                i.setBackgroundColor(requireContext.resources.getColor(R.color.white))
            }
            it.setBackgroundColor(requireContext.resources.getColor(R.color.selected_saved_item))

            Log.d(TAG_SAVE_LIST_ADAPTER, "Нажат холдер: $holder")
            Log.d(TAG_SAVE_LIST_ADAPTER, "Нажат холдер: $currentStringLocationInList")

            listener.onClick(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveLocationHolder {
        val viewForHolder = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        val problemHolderInstance = SaveLocationHolder(viewForHolder)

        return problemHolderInstance
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_drawer
    }

    interface SaveClickListener{
        fun onClick(string: String)
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