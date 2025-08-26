package com.example.pennypin.utils

import android.util.Log
import com.example.pennypin.data.Transaction

class SmsParser {
    fun parseSms(body: String, sender: String): Any? {
        val lowerbody=body.lowercase()

        if(!lowerbody.contains("debited")&&!lowerbody.contains("credited")&&!lowerbody.contains("received")){
            Log.d("SmsReceiver", "Unknown transaction type")
            return null
        }

        Log.d("SmsReceiver", "Parsing message: $lowerbody")
        val amountreg=Regex("""(?:rs\.?|inr)[ ]?([\d,]+\.?\d*)""")
        val amount=amountreg.find(lowerbody)
        val match=amount?.groups?.get(1)?.value?.replace(",","")?.toDoubleOrNull() ?: return null

        val description=when{
            body.contains("UPI", ignoreCase = true) -> "UPI Transaction"
            body.contains("POS", ignoreCase = true) -> "Card Payment"
            body.contains("ATM", ignoreCase = true) -> "ATM Withdrawal"
            body.contains("credited", ignoreCase = true) -> "Amount Credited"
            else -> "Bank Transaction"
        }

        val date= System.currentTimeMillis()
        return Transaction(
            date=date,
            description=description,
            amount=match
        )

    }
}