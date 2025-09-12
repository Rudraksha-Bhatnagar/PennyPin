package com.example.pennypin.data


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile WHERE uid = :uid LIMIT 1")
    fun getUserProfileFlow(uid: String): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE uid = :uid LIMIT 1")
    suspend fun getUserProfile(uid: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: UserProfile)

    @Update
    suspend fun update(profile: UserProfile)

    @Query("DELETE FROM user_profile WHERE uid = :uid")
    suspend fun deleteByUid(uid: String)

    @Query("DELETE FROM user_profile")
    suspend fun clearAll()
}
