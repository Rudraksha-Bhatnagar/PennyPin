package com.example.pennypin.receiver

import android.content.BroadcastReceiver
import androidx.work.OneTimeWorkRequestBuilder
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.pennypin.utils.SmsProcessingService // Make sure this import is correct

class SmsReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "SmsReceiver" // Define a TAG for logging
    }

        override fun onReceive(context: Context, intent: Intent?) {
            Log.d(TAG, "onReceive CALLED! Action: ${intent?.action}") // Main test log

            if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
                Log.d(TAG, "SMS_RECEIVED_ACTION detected.")
                // Optionally, try to get messages but keep it simple
                val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                if (smsMessages == null || smsMessages.isEmpty()) {
                    Log.e(TAG, "No SMS messages found in intent or messages array is null/empty.")
                    return
                }
                for (message in smsMessages) {
                    Log.d(TAG, "Message Body: ${message.messageBody}")
                    Log.d(TAG, "Message Sender: ${message.originatingAddress}")
                }
            } else {
                Log.w(TAG, "Received intent with unexpected action: ${intent?.action}")
            }
        }
    }

//    override fun onReceive(context: Context, intent: Intent?) {
//        Log.d(TAG, "onReceive CALLED! Action: ${intent?.action}") // VERY FIRST LOG
//        try {
//            if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
//                Log.d(TAG, "SMS_RECEIVED_ACTION detected.")
//
//                val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
//                if (smsMessages == null || smsMessages.isEmpty()) {
//                    Log.e(TAG, "No SMS messages found in intent or messages array is null/empty.")
//                    return // Exit if no messages
//                }
//
//                val sb = StringBuffer()
//                var sender = ""
//                for (message in smsMessages) {
//                    val msgBody = message.messageBody
//                    val originatingAddress = message.originatingAddress
//
//                    if (msgBody != null) {
//                        sb.append(msgBody)
//                    } else {
//                        Log.w(TAG, "Message body is null for a part of SMS.")
//                    }
//
//                    if (originatingAddress != null) {
//                        sender = originatingAddress // This will take the sender of the last part if multi-part
//                    } else {
//                        Log.w(TAG, "Originating address is null for a part of SMS.")
//                    }
//                }
//                val body = sb.toString()
//
//                // Only log if we have something meaningful, or explicitly log empty
//                if (sender.isNotBlank() || body.isNotBlank()) {
//                    Log.d(TAG, "Sender: $sender\nBody: $body")
//                } else {
//                    Log.w(TAG, "Extracted sender and body are both blank. SMS parts might have been empty or null.")
//                }
//
//
//                // Check if context is valid before using it for WorkManager
//                // (though for a receiver, context is generally always valid)
//                if (context != null) {
//                    val work = OneTimeWorkRequestBuilder<SmsProcessingService>()
//                        .setInputData(workDataOf("body" to body, "sender" to sender))
//                        .build()
//                    WorkManager.getInstance(context).enqueue(work)
//                    Log.d(TAG, "WorkManager enqueued for SMS.")
//                } else {
//                    Log.e(TAG, "Context is null, cannot enqueue WorkManager.")
//                }
//
//            } else {
//                Log.w(TAG, "Received intent with unexpected action: ${intent?.action}")
//            }
//        } catch (e: Throwable) {
//            Log.e(TAG, "CRITICAL ERROR in onReceive: ${e.message}", e) // Log any exception
//        }
//        catch(e: SecurityException){
//            Log.e(TAG, "SECURITY EXCEPTION in onReceive: ")
//        }
//    }

