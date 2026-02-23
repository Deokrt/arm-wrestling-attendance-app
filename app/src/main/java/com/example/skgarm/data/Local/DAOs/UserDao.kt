package com.example.skgarm.data.Local.DAOs

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skgarm.data.Local.Entity.User
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