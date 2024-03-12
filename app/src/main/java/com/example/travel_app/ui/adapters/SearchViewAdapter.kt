package com.example.travel_app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_app.R
import com.example.travel_app.model.TransactionsData

class SearchViewAdapter (var mList: List<TransactionsData>) :
    RecyclerView.Adapter<SearchViewAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val image : ImageView = itemView.findViewById(R.id.image)
        val title : TextView = itemView.findViewById(R.id.title)
    }

    fun setFilteredList(mList: List<TransactionsData>){
        this.mList = mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchViewAdapter.TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_items, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewAdapter.TransactionViewHolder, position: Int) {
        holder.image.setImageResource(mList[position].image)
        holder.title.text = mList[position].title
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}