package com.example.pennypin.repository

import androidx.lifecycle.LiveData
import com.example.pennypin.data.Transaction
import com.example.pennypin.data.TransactionDao

class TransactionRepository(private val dao: TransactionDao) {

    val allTransactions: LiveData<List<Transaction>> = dao.getAllTransactions()

    suspend fun insert(transaction: Transaction) {
        dao.insert(transaction)
    }

    suspend fun delete(transaction: Transaction) {
        dao.delete(transaction)
    }
}
