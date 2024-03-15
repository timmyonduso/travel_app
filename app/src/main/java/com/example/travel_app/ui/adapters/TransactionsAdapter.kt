package com.example.travel_app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_app.model.CashCollection
import com.example.travel_app.R

class TransactionsAdapter(private val transactions: MutableList<CashCollection>,
                          private val onClickListener: (CashCollection) -> Unit
) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {



    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fromLocation: TextView = itemView.findViewById(R.id.text_transaction_from)
        val toLocation: TextView = itemView.findViewById(R.id.text_transaction_to)
        val time: TextView = itemView.findViewById(R.id.text_transaction_time)
        val nameTextView: TextView = itemView.findViewById(R.id.text_transaction_name)
        val fare: TextView = itemView.findViewById(R.id.text_transaction_fare)
        val id: TextView = itemView.findViewById(R.id.text_transaction_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.itemView.setOnClickListener {
            onClickListener(transaction)
        }
        holder.fromLocation.text = "From: ${transaction.fromLocation}"
        holder.toLocation.text = "To: ${transaction.toLocation}"
        holder.time.text = transaction.timestamp.toString()
        holder.fare.text = String.format("%.2f", transaction.fare) // Format fare with 2 decimal places

        holder.id.text = "Transaction ID: ${transaction.transactionKey}" // Assuming you have a TextView for the key

// Check if username exists in transaction
        if (transaction.userId != null) {
            holder.nameTextView.text = transaction.userId
        } else {
            // Handle case where username is unavailable (use email or "Unknown User")
            holder.nameTextView.text = transaction.userId ?: "Unknown User"
        }
    }

    override fun getItemCount(): Int {
        return transactions.size
    }
}
