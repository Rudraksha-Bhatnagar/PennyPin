package com.example.pennypin.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pennypin.R
import com.example.pennypin.adapter.TransactionAdapter
import com.example.pennypin.viewmodel.TransactionViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.pennypin.utils.InboxReader
import com.example.pennypin.utils.SmsParser
import com.google.android.material.progressindicator.CircularProgressIndicator

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var adapter: TransactionAdapter
    private lateinit var viewModel: TransactionViewModel

    companion object {
        private const val SMS_PERMISSION_REQUEST_CODE = 1
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        fab = findViewById(R.id.floatingActionButton)
        recyclerView = findViewById(R.id.recyclerview)

        // Initialize adapter with empty list
        adapter = TransactionAdapter(mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        // Observe transactions

        viewModel.setDateRangeForCurrentMonth()  // Add to XML

        val circularProgressBar = findViewById<CircularProgressIndicator>(R.id.monthlyProgress)
        val amountTextView:TextView = findViewById(R.id.tvAmount)
        viewModel.totalSpentForRange.observe(this) { spent ->
            val totalSpentValue = spent ?: 0.0
            amountTextView.text=totalSpentValue.toString()
        }

        circularProgressBar.setProgress(100,true)

        amountTextView.setOnClickListener{
            startActivity(Intent(this,TransactionHistory::class.java))
        }
        viewModel.transactionsForRange.observe(this) { transactions ->
            Log.d(TAG, "Received ${transactions?.size ?: 0} transactions from database")
            transactions?.let {
                adapter.updateTransactions(it)
            }
        }
            // Set up FAB click listener
        fab.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        // Request permissions
        requestSmsPermissions()

        // Request battery optimization exemption
        requestBatteryOptimizationExemption()

    }


    private fun requestSmsPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            Log.d(TAG, "Requesting SMS permissions: $permissionsToRequest")
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                SMS_PERMISSION_REQUEST_CODE
            )
        } else {
            Log.d(TAG, "SMS permissions already granted")
            // 🔥 Read inbox immediately if already granted
            InboxReader(this, viewModel).readLatestSms()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            if (allGranted) {
                Log.d(TAG, "All SMS permissions granted")
                Toast.makeText(this, "SMS permissions granted.", Toast.LENGTH_LONG).show()

                // 🔥 Start inbox reading
                InboxReader(this, viewModel).readLatestSms()
            } else {
                Log.w(TAG, "Some SMS permissions were denied")
                Toast.makeText(this, "SMS permissions required.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun requestBatteryOptimizationExemption() {
        val packageName = packageName
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager

        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            try {
                val intent = Intent()
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
                Log.d(TAG, "Requested battery optimization exemption")
            } catch (e: Exception) {
                Log.e(TAG, "Error requesting battery optimization exemption", e)
            }
        } else {
            Log.d(TAG, "App already exempt from battery optimization")
        }
    }


}