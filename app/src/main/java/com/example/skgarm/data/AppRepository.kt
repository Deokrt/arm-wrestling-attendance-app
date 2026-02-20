package com.example.skgarm.data

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppRepository @Inject constructor(

    private val userDao: UserDao,
    private val availabilityDao: AvailabilityDao
) {
}