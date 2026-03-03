package com.example.skgarm.data.Local.DAOs

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skgarm.data.Local.Entity.Availability
import kotlinx.coroutines.flow.Flow

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
    fun isUserAvailableFlow(
        date: String,
        timeSlot: String,
        email: String
    ): Flow<Int> // The parameters on this fun will be on the params on the query
}