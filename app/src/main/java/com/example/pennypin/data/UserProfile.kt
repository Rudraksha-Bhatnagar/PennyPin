package com.example.pennypin.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val uid: String,
    val name: String?,
    val email: String,
    val upiId: String
)
