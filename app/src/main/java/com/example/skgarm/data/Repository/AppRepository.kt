package com.example.skgarm.data.Repository

import com.example.skgarm.data.Local.Entity.User

interface AppRepository {

    suspend fun registerUser(name : String , email : String  , password : String): Result<User>
    suspend fun signIn(email : String  , password : String): Result<User>

}