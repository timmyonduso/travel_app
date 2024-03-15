package com.example.travel_app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_app.R
import com.example.travel_app.databinding.FragmentTransactionsBinding
import com.example.travel_app.model.CashCollection
import com.example.travel_app.model.TransactionsData
import com.example.travel_app.ui.activity.UpdateActivity
import com.example.travel_app.ui.adapters.SearchViewAdapter
import com.example.travel_app.ui.adapters.TransactionsAdapter
import com.example.travel_app.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.Serializable
//import com.example.travel_app.ui.adapters.SearchViewAdapter
import java.util.Locale


class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionsAdapter: TransactionsAdapter

    private lateinit var transactionsList: MutableList<CashCollection> // Declare outside the function

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        transactionsList = mutableListOf() // Initialize here

        fetchTransactionsFromFirebase() // Call data fetching first

        transactionsAdapter = TransactionsAdapter(transactionsList) { transaction ->
            val intent = Intent(context, UpdateActivity::class.java)
            intent.putExtra("transactionId", transaction.transactionKey)
            intent.putExtra("transactionFare", transaction.fare)
            intent.putExtra("transactionName", transaction.userId)
            intent.putExtra("transactionRoute", "${transaction.fromLocation} - ${transaction.toLocation}")
            startActivity(intent)
        }

        binding.transactionsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.transactionsRecyclerView.adapter = transactionsAdapter



        val view = binding.root
        return view
    }

    private fun fetchTransactionsFromFirebase() {
        Utils.initializeFirebase()
        val transactionsRef = Utils.database.getReference("transactions")

        transactionsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                transactionsList.clear() // Clear existing data (if any)
                for (childSnapshot in snapshot.children) {
                    val transaction = childSnapshot.getValue(CashCollection::class.java)
                    transactionsList.add(transaction!!) // Ensure non-null
                }
                transactionsAdapter.notifyDataSetChanged() // Update adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error fetching transactions
                Log.w("TransactionsFragment", "Error fetching transactions: ${error.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}