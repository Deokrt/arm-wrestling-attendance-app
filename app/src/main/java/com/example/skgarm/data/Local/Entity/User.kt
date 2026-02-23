package com.example.skgarm.data.Local.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class User(

    @PrimaryKey
    var name: String,
    var email: String,
    val password: String
)




