package com.example.pennypin.ui

import android.content.ContentValues.TAG
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

import com.example.pennypin.R
import com.example.pennypin.adapter.TransactionAdapter

import com.example.pennypin.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.text.format

class TransactionHistory : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var viewModel: TransactionViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val startDateButton: Button = findViewById(R.id.button) // Add to XML
        val endDateButton: Button = findViewById(R.id.button4)   // Add to XML
        val applyRangeButton: Button = findViewById(R.id.button3) // Add to XML

        recyclerView = findViewById(R.id.recyclerview)

        // Initialize adapter with empty list
        adapter = TransactionAdapter(mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        var selectedStartDate: Long? = null
        var selectedEndDate: Long? = null


        startDateButton?.setOnClickListener {
            // Show DatePickerDialog, update selectedEndDate and button text
            showDatePickerDialog { year, month, dayOfMonth ->
                val calendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                    setEndOfDay(this) // helper function that sets time to 23:59:59
                }
                selectedStartDate = calendar.timeInMillis

                // Convert Long -> java.util.Date -> format
                startDateButton.text = SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                ).format(Date(selectedStartDate!!))
            }
        }

        endDateButton?.setOnClickListener {
            // Show DatePickerDialog, update selectedEndDate and button text
            showDatePickerDialog { year, month, dayOfMonth ->
                val calendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                    setEndOfDay(this) // helper function that sets time to 23:59:59
                }
                selectedEndDate = calendar.timeInMillis

                // Convert Long -> java.util.Date -> format
                endDateButton.text = SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                ).format(Date(selectedEndDate!!))
            }
        }
        applyRangeButton.setOnClickListener {
            if (selectedStartDate != null && selectedEndDate != null && selectedStartDate!! <= selectedEndDate!!) {
                viewModel.updateDateRange(selectedStartDate!!, selectedEndDate!!)
            } else {
                Toast.makeText(this, "Please select a valid date range", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.transactionsForRange.observe(this) { transactions ->
            Log.d(TAG, "Received ${transactions?.size ?: 0} transactions from database")
            transactions?.let {
                adapter.updateTransactions(it)
            }
        }
    }
    private fun showDatePickerDialog(onDateSet: (year: Int, month: Int, dayOfMonth: Int) -> Unit) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = android.app.DatePickerDialog(
            this, // or requireContext() if inside Fragment
            { _, year, month, dayOfMonth ->
                onDateSet(year, month, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }


    // Helper methods for midnight/end of day (can be in Activity or ViewModel extension)
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
}