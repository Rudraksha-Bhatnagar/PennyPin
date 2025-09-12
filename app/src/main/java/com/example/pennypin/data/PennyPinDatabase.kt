package com.example.pennypin.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Transaction::class, UserProfile::class],
    version = 2, // bumped version since we added a new table
    exportSchema = false
)
abstract class PennyPinDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: PennyPinDatabase? = null

        fun getDatabase(context: Context): PennyPinDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PennyPinDatabase::class.java,
                    "pennypin_database"
                )
                    // wipes db on schema change (safe for dev, remove in prod)
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
