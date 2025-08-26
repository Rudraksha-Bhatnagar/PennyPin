package com.example.pennypin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.pennypin.data.PennyPinDatabase
import com.example.pennypin.data.Transaction
import com.example.pennypin.repository.TransactionRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TransactionViewModel(application: Application):AndroidViewModel(application) {
    private val repository: TransactionRepository
    val allTransactions: LiveData<List<Transaction>>
    init {
        val dao = PennyPinDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(dao)
        allTransactions = repository.allTransactions
    }
    fun insert(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(transaction)
    }
    fun delete(transaction: Transaction) = viewModelScope.launch {
        repository.delete(transaction)
}
}