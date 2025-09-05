package com.example.pennypin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.pennypin.R
import com.example.pennypin.data.Transaction
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(private var transactionList: MutableList<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptiontextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amounttextView)
        val dateTextView: TextView = itemView.findViewById(R.id.datetextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.descriptionTextView.text = transaction.description
        holder.amountTextView.text = "₹${transaction.amount}"
        holder.dateTextView.text = formatDate(transaction.date)
    }

    override fun getItemCount() = transactionList.size

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    // Efficient update for entire list
    fun updateTransactions(newTransactions: List<Transaction>) {
        val diffCallback = TransactionDiffCallback(transactionList, newTransactions)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        transactionList.clear()
        transactionList.addAll(newTransactions)
        diffResult.dispatchUpdatesTo(this)
    }

    // Update one transaction
    fun updateTransaction(updatedTransaction: Transaction) {
        val newList = transactionList.toMutableList()
        val index = transactionList.indexOfFirst { it.id == updatedTransaction.id }

        if (index != -1) {
            newList[index] = updatedTransaction
            updateTransactions(newList)
        }
    }

    // Add new transaction
    fun addTransaction(newTransaction: Transaction) {
        val newList = transactionList.toMutableList()
        newList.add(0, newTransaction)
        updateTransactions(newList)
    }

    // Delete transaction
    fun deleteTransaction(transactionId: Long) {
        val newList = transactionList.filter { it.id != transactionId }
        updateTransactions(newList)
    }
}
