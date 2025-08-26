package com.example.pennypin.adapter

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.pennypin.R
import com.example.pennypin.data.Transaction
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

class TransactionAdapter(private val transactionList: List<Transaction>) :RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptiontextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amounttextView)
        val dateTextView: TextView = itemView.findViewById(R.id.datetextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction,parent,false)
        return TransactionViewHolder(view)
    }
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.descriptionTextView.text = transaction.description
        holder.amountTextView.text = transaction.amount.toString()
        holder.dateTextView.text = transaction.date.toString()

    }

    override fun getItemCount()= transactionList.size

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun updateTransactions(newTransactions: List<Transaction>?){
        var transactions = newTransactions
        notifyDataSetChanged()
    }
}