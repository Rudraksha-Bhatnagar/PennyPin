package com.example.pennypin.utils

import android.util.Log
import com.example.pennypin.data.Transaction

class SmsParser {
    fun parseSms(body: String, sender: String, date: Long,smsId:Long): Transaction? { // ✅ accept date
        val lowerBody = body.lowercase()

        Log.d("SmsParser", "Parsing SMS from: $sender")
        Log.d("SmsParser", "SMS Body: $body")

        // Check if it's a financial transaction SMS
        if (!isFinancialSms(lowerBody, sender)) {
            Log.d("SmsParser", "Not a financial SMS")
            return null
        }

        // Extract amount using multiple regex patterns
        val amount = extractAmount(body)
        if (amount == null) {
            Log.d("SmsParser", "Could not extract amount")
            return null
        }

        // Determine transaction type and description
        val description = determineDescription(body)

        // Determine if it's debit or credit
        val finalAmount = if (isDebitTransaction(lowerBody)) -amount else amount

        // ✅ Use actual SMS date instead of System.currentTimeMillis()
        val transaction = Transaction(
            date = date,
            description = description,
            amount = finalAmount,
            id=smsId
        )

        Log.d("SmsParser", "Successfully parsed transaction: $transaction")
        return transaction
    }

    private fun isFinancialSms(lowerBody: String, sender: String): Boolean {
        val financialKeywords = listOf(
            "debited", "credited", "sent", "withdraw",
            "deposit", "transfer"
        )

        val bankPatterns = listOf(
            "bank", "hdfc", "sbi", "icici", "axis", "kotak", "paytm",
            "gpay", "phonepe", "amazon", "flipkart"
        )

        // Check if sender looks like a bank/financial institution
        val isFromBank = bankPatterns.any { pattern ->
            sender.contains(pattern, ignoreCase = true)
        }

        // Check if message contains financial keywords
        val hasFinancialKeywords = financialKeywords.any { keyword ->
            lowerBody.contains(keyword)
        }

        return hasFinancialKeywords || isFromBank
    }

    private fun extractAmount(body: String): Double? {
        val patterns = listOf(
            Regex("""rs\.?\s*([0-9,]+\.?[0-9]*)""", RegexOption.IGNORE_CASE),
            Regex("""inr\s*([0-9,]+\.?[0-9]*)""", RegexOption.IGNORE_CASE),
            Regex("""₹\s*([0-9,]+\.?[0-9]*)"""),
            Regex("""amount[:\s]+rs\.?\s*([0-9,]+\.?[0-9]*)""", RegexOption.IGNORE_CASE),
            Regex("""(?:^|\s)([0-9]{1,3}(?:,[0-9]{3})*(?:\.[0-9]{2})?)(?:\s|$)""")
        )

        for (pattern in patterns) {
            val match = pattern.find(body)
            if (match != null) {
                val amountStr = match.groups[1]?.value?.replace(",", "")
                val amount = amountStr?.toDoubleOrNull()
                if (amount != null && amount > 0) {
                    Log.d("SmsParser", "Found amount: $amount using pattern: ${pattern.pattern}")
                    return amount
                }
            }
        }

        Log.d("SmsParser", "No amount found in: $body")
        return null
    }

    private fun isDebitTransaction(lowerBody: String): Boolean {
        val debitKeywords = listOf("debited", "withdrawn", "paid", "sent")
        return debitKeywords.any { keyword -> lowerBody.contains(keyword) }
    }

    private fun determineDescription(body: String): String {
        val lowerBody = body.lowercase()

        return when {
            lowerBody.contains("upi") -> "UPI Transaction"
            lowerBody.contains("pos") -> "Card Payment (POS)"
            lowerBody.contains("atm") -> "ATM Withdrawal"
            lowerBody.contains("neft") || lowerBody.contains("rtgs") || lowerBody.contains("imps") -> "Bank Transfer"
            lowerBody.contains("credited") || lowerBody.contains("received") -> "Amount Credited"
            lowerBody.contains("debited") || lowerBody.contains("withdrawn") -> "Amount Debited"
            lowerBody.contains("online") -> "Online Payment"
            else -> "Bank Transaction"
        }
    }
}
