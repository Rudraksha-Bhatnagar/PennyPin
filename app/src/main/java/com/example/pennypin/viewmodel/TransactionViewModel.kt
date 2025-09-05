package com.example.pennypin.viewmodel

import android.app.Application
import android.icu.util.Calendar
import android.view.animation.Transformation
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.*
import com.example.pennypin.data.PennyPinDatabase
import com.example.pennypin.data.Transaction
import com.example.pennypin.repository.TransactionRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TransactionViewModel(application: Application):AndroidViewModel(application) {
    private val repository: TransactionRepository
    val allTransactions: LiveData<List<Transaction>>
    private val _dateRange = MutableLiveData<Pair<Long, Long>>()



    init {
        val dao = PennyPinDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(dao)
        allTransactions = repository.allTransactions

    }
    val totalSpentForRange: LiveData<Double?> = _dateRange.switchMap { range ->
        if (range != null) {
            repository.getTotalForDateRange(range.first, range.second)
        } else {
            MutableLiveData<Double?>().apply { value = null } // Or some default like all-time total
        }
    }
    val transactionsForRange: LiveData<List<Transaction>> = _dateRange.switchMap { range ->
        if (range != null) {
            repository.getTransactionsForDateRange(range.first, range.second)
        } else {
            MutableLiveData<List<Transaction>>().apply { value = emptyList() }
        }
    }


    fun updateDateRange(startDate: Long, endDate: Long) {
        _dateRange.value = Pair(startDate, endDate)
    }
    fun setDateRangeForCurrentMonth() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        setMidnight(calendar)
        val startDate = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        setEndOfDay(calendar)
        val endDate = calendar.timeInMillis

        updateDateRange(startDate, endDate)
    }
    fun setDateRangeForAllTime() {

        val veryStartDate = 0L // Epoch start
        val veryEndDate = Calendar.getInstance().apply { add(Calendar.YEAR, 100) }.timeInMillis // Far future
        updateDateRange(veryStartDate, veryEndDate)
    }

    private fun setMidnight(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    private fun setEndOfDay(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
    }


    fun insert(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(transaction)
    }
    fun delete(transaction: Transaction) = viewModelScope.launch {
        repository.delete(transaction)
    }




}