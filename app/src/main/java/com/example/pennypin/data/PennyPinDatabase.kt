package com.example.pennypin.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
abstract class PennyPinDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: PennyPinDatabase? = null

        fun getDatabase(context: Context): PennyPinDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PennyPinDatabase::class.java,
                    "pennypin_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
