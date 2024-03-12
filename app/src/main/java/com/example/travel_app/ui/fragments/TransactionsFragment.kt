package com.example.travel_app.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_app.R
import com.example.travel_app.databinding.FragmentTransactionsBinding
import com.example.travel_app.model.TransactionsData
import com.example.travel_app.ui.adapters.SearchViewAdapter
import java.util.Locale


class TransactionsFragment : Fragment() {
    private var mList = ArrayList<TransactionsData>()
    private lateinit var adapter: SearchViewAdapter
    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

//    private fun filterList(query: String?){
//
//        if (query != null){
//            val filteredList = ArrayList<TransactionsData>()
//            for (i in mList){
//                if (i.title.lowercase(Locale.ROOT).contains(query)){
//                    filteredList.add(i)
//                }
//            }
//
//            if (filteredList.isEmpty()){
//                Toast.makeText(
//                    requireContext(),
//                    "Sorry there was an error. Please try again",
//                    Toast.LENGTH_LONG
//                ).show()
//            } else{
//                adapter.setFilteredList(filteredList)
//            }
//        }
//    }

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
//
//        binding.recyclerView.setHasFixedSize(true)
//        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
//
//        addDataToList()
//
//        adapter = SearchViewAdapter(mList)
//        binding.recyclerView.adapter = adapter
//
//        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                filterList(newText)
//                return true
//            }
//
//        })

        val view = binding.root
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}