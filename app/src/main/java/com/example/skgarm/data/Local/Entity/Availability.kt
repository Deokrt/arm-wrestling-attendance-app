package com.example.skgarm.data.Local.Entity

import androidx.room.Entity


@Entity(
    tableName = "availability",

    primaryKeys = ["date", "timeSlot", "userEmail"]
)
data class Availability(
    var date : String ,
    var timeSlot : String ,
    var userEmail : String,
    val userName : String

)
