package com.example.pennypin.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pennypin.data.PennyPinDatabase
import com.example.pennypin.data.Transaction

class SmsProcessingService(
    appContext: Context,
    workerParams: WorkerParameters
): CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        Log.d("SmsProcessingService", "doWork CALLED!")
        val body=inputData.getString("body")?:return Result.failure()
        val sender=inputData.getString("sender")?: return Result.failure()

        val db=PennyPinDatabase.getDatabase(applicationContext)
        val dao=db.transactionDao()
        val parsed:Any? =SmsParser().parseSms(body,sender)
        val transaction=parsed as? Transaction
        if(transaction!=null){
            dao.insert(transaction)
        }
        return Result.success()

    }
}