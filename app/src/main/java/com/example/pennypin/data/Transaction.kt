package com.example.pennypin.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey val id: Long,
    val date: Long,
    val description: String,
    val amount: Double)
