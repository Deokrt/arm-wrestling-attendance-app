package com.example.skgarm.data.Local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.skgarm.data.Local.DAOs.AvailabilityDao
import com.example.skgarm.data.Local.DAOs.UserDao
import com.example.skgarm.data.Local.Entity.Availability
import com.example.skgarm.data.Local.Entity.User
import com.example.skgarm.data.UserPreferences

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(
    entities = [User::class, Availability::class], // here we have all the tables
    version = 1 ,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun userDAO() : UserDao  // we access dao through here
    abstract fun availabilityDAO() : AvailabilityDao
}

// ---------- HILT -----------

@Module
@InstallIn(SingletonComponent::class)
object  AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context : Context) : AppDatabase
    = Room.databaseBuilder(
        context , AppDatabase::class.java,"armskg.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun providesUserDao(db : AppDatabase) : UserDao = db.userDAO()

    @Provides
    fun providesAvailabilityDao(db : AppDatabase) : AvailabilityDao = db.availabilityDAO()

    @Provides
    @Singleton
    fun provideUserPreferences(
        @ApplicationContext context: Context
    ): UserPreferences = UserPreferences(context)


}