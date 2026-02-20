package com.example.skgarm.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): User?

    @Query("SELECT * FROM users")
    fun getAllFlow(): Flow<List<User>>
    /* Flow<List<User>> → gives you the data NOW and then keeps watching.
    Every time the table changes it automatically
    sends you the new list. Your UI updates automatically. */

}


@Dao
interface AvailabilityDao {

    /** Mark a user available for a slot */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markAvailable(availability: Availability)

    /** Remove a user's availability for a slot */
    @Query(
        "DELETE FROM availability WHERE date = :date AND timeSlot = :timeSlot AND userEmail = :email"
    )
    suspend fun markUnavailable(date: String, timeSlot: String, email: String)

    /** All attendees for a specific slot – reactive */
    @Query(
        "SELECT * FROM availability WHERE date = :date AND timeSlot = :timeSlot"
    )
    fun getAttendeesFlow(date: String, timeSlot: String): Flow<List<Availability>>

    /** Check if a specific user is available for a slot */
    @Query(
        "SELECT COUNT(*) FROM availability WHERE date = :date AND timeSlot = :timeSlot AND userEmail = :email"
    )
    fun isUserAvailableFlow(date: String, timeSlot: String, email: String): Flow<Int>
}