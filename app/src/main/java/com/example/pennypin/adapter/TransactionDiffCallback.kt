package com.example.pennypin.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.pennypin.data.Transaction

class TransactionDiffCallback(
    private val oldList: List<Transaction>,
    private val newList: List<Transaction>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Use unique ID if available, else compare by description+date+amount
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
