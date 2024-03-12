package com.example.travel_app.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_app.R
import com.example.travel_app.model.TransactionsData
import com.example.travel_app.ui.adapters.SearchViewAdapter
import java.util.Locale

class SearchViewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var mList = ArrayList<TransactionsData>()
    private lateinit var adapter: SearchViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        addDataToList()

        adapter = SearchViewAdapter(mList)
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })

    }

private fun filterList(query: String?){

    if (query != null){
        val filteredList = ArrayList<TransactionsData>()
        for (i in mList){
            if (i.title.lowercase(Locale.ROOT).contains(query)){
                filteredList.add(i)
            }
        }

        if (filteredList.isEmpty()){
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
        } else{
            adapter.setFilteredList(filteredList)
        }
    }
}

    private fun addDataToList() {
        mList.add(TransactionsData("Timmy", R.drawable.bus))
        mList.add(TransactionsData("Teddy", R.drawable.bus))
        mList.add(TransactionsData("Tonny", R.drawable.bus))
        mList.add(TransactionsData("Bev", R.drawable.bus))
        mList.add(TransactionsData("Nicky", R.drawable.bus))
        mList.add(TransactionsData("Jim", R.drawable.bus))
        mList.add(TransactionsData("Joe", R.drawable.bus))
        mList.add(TransactionsData("Jennie", R.drawable.bus))
        mList.add(TransactionsData("Julie", R.drawable.bus))

    }
}