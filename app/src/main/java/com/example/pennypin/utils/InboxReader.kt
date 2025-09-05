package com.example.pennypin.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.pennypin.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InboxReader(private val context: Context, private val viewModel: TransactionViewModel) {

    companion object {
        private const val TAG = "InboxReader"
    }

    fun readLatestSms() {
        try {
            val uri = Uri.parse("content://sms/inbox")
            val projection = arrayOf("_id", "address", "body", "date")

            val cursor = context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                "date DESC" // sorting only, "LIMIT" won't work here in ContentResolver
            )

            cursor?.use {
                var count = 0
                while (it.moveToNext() && count < 20) { // ✅ manually limit to 20
                    val id = it.getLong(it.getColumnIndexOrThrow("_id"))
                    val sender = it.getString(it.getColumnIndexOrThrow("address"))
                    val body = it.getString(it.getColumnIndexOrThrow("body"))
                    val dateMillis = it.getLong(it.getColumnIndexOrThrow("date"))

                    // ✅ Convert timestamp -> human readable

                    Log.d(TAG, "Inbox SMS -> Sender: $sender, Body: $body, Date: $dateMillis")

                    // Parse with your SmsParser (you may want to update parseSms to accept date too)
                    val parser = SmsParser()
                    val transaction = parser.parseSms(body, sender, dateMillis,id) // pass date here

                    if (transaction != null) {
                        viewModel.insert(transaction)
                        Log.d(TAG, "✅ Parsed & inserted transaction: $transaction")
                    }
                    count++
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading inbox: ${e.message}", e)
        }
    }

}
