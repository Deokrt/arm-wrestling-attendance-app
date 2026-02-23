package com.example.skgarm.data.Repository

import com.example.skgarm.data.Local.DAOs.AvailabilityDao
import com.example.skgarm.data.Local.DAOs.UserDao
import com.example.skgarm.data.Local.Entity.Availability
import com.example.skgarm.data.Local.Entity.User
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppRepositoryImpl @Inject constructor(

    private val userDao: UserDao,
    private val availabilityDao: AvailabilityDao
) : AppRepository {


    override suspend fun registerUser(name : String , email : String , password : String) : Result<User>{


        // answer to "what can go wrong"

        if (name.isEmpty()){
            return Result.failure(Exception("Name can't be empty"))
        }
        if ( password.length < 6 ){
            return Result.failure(Exception("Password too short"))
        }

        if (! (name.endsWith("@gmail.com")) ){
            return Result.failure(Exception("Invalid email"))
        }
        val existing = userDao.getByEmail(email)

        if (existing != null){
            return Result.failure(Exception("Email already registered"))
        }

        val user = User(email.trim().lowercase(), name.trim(), password)
        userDao.insert(user)
       return Result.success(user)

    }

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<User> {

        val user = userDao.getByEmail(email.trim().lowercase())
            ?: return Result.failure(Exception("No account found. Join the team first."))



        if (user.password != password){
            return Result.failure(Exception("Wrong password"))
        }

        return Result.success(user)

    }

    suspend fun markAvailable(date : String , timeSlot : String, user : User){
        availabilityDao.markAvailable(

            Availability(date = date , timeSlot = timeSlot , userEmail = user.email , userName = user.name)
        )
    }

    suspend fun markUnavailable(date : String , timeSlot : String, userEmail : String){
        availabilityDao.markUnavailable(date , timeSlot , userEmail)


    }


}