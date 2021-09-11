package com.example.weatherapp.View.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.ViewModel.WeatherViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SearchFragment: Fragment(R.layout.fragment_search), SearchAdapter.ClickListener {

    private val TAG_SEARCH_FRAGMENT = "MySearchFragment"

    private lateinit var searchEditTextLayout: TextInputLayout
    private lateinit var searchEditText: TextInputEditText
    private lateinit var searchButton: ImageButton

    // RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchAdapter

    private lateinit var viewModel: WeatherViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchEditTextLayout = view.findViewById(R.id.id_textinput_layout)
        searchEditText = view.findViewById(R.id.id_search_edittext)
        searchButton = view.findViewById(R.id.id_search_button)
        recyclerView = view.findViewById(R.id.id_search_recyclerview)

        init()
        setEditText()
        setEditTextLayout()
        setSearchButton()
        setSearchLiveData()
    }

    fun init(){
        setRecycler()

        viewModel = (activity as MainActivity).activityViewModel
    }

    fun setRecycler(){
        val layoutManager = LinearLayoutManager(requireContext())
        adapter = SearchAdapter(this)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    fun setEditText(){
        searchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                searchEditTextLayout.isErrorEnabled = false
                if(s.toString().length >= 3){
                    viewModel.searchLocations(s.toString())
                } else{
                    adapter.setList(emptyList())
                }
            }

        })
    }

    fun setEditTextLayout(){

    }

    fun setSearchButton(){
        searchButton.setOnClickListener {
            if(searchEditText.text.toString().length < 3){
                val textError = getString(R.string.short_search_text)
                searchEditTextLayout.error = textError
                searchEditTextLayout.isErrorEnabled = true
            } else{
                viewModel.searchLocations(searchEditText.text.toString())
            }
        }
    }

    fun nothingFound(){
        searchEditTextLayout.isErrorEnabled = true
        searchEditTextLayout.error = getString(R.string.nothing_found)
    }

    fun setSearchLiveData(){
        viewModel.searchLiveData.observe(viewLifecycleOwner){
            if(it.isEmpty()){
                Log.d(TAG_SEARCH_FRAGMENT, "Во фрамент пришёл пустой массив: $it")
                adapter.setList(it)
                nothingFound()
            } else{
                adapter.setList(it)
            }
        }
    }

    override fun onClick(string: String) {
        (activity as MainActivity).openNewSearchLocationFragment(string, searchEditText)
    }

}