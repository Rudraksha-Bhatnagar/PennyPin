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
import com.example.pennypin.data.Transaction
import com.example.pennypin.viewmodel.TransactionViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.PowerManager
import android.os.StrictMode
import android.provider.Settings

import android.app.Application
import android.os.Build
import android.provider.Telephony


class MainActivity: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var fab: FloatingActionButton

    private lateinit var adapter: TransactionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // In your Application class's onCreate method


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS
            ), 1)
        }
        val intent = Intent()
        val packageName = packageName
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager

        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }

        setContentView(R.layout.activity_main)
        adapter = TransactionAdapter(emptyList())

        fab= findViewById(R.id.floatingActionButton)
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.adapter = adapter
        recyclerView.layoutManager= LinearLayoutManager(this)

        fab.setOnClickListener {
            startActivity(Intent(this,AddTransactionActivity::class.java))
        }
        val viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        viewModel.allTransactions.observe(this) { transactions ->

            adapter.updateTransactions(transactions)

        }






    }



}