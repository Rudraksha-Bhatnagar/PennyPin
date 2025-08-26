package com.example.pennypin.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TransactionDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: Transaction)
    @Delete
    suspend fun delete(transaction: Transaction)
//    @Update
//    suspend fun update(vararg transaction: Transaction)
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): LiveData<List<Transaction>>

}