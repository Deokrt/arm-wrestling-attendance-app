package com.example.skgarm.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.datastore.preferences.preferencesDataStore
import com.example.skgarm.data.Local.Entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val EMAIL_KEY = stringPreferencesKey("user_email")
    private val NAME_KEY = stringPreferencesKey("user_name")

    suspend fun saveUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[NAME_KEY] = user.name
        }
    }

    val savedUser: Flow<User?> = context.dataStore.data
        .map { preferences ->
            val email = preferences[EMAIL_KEY]
            val name = preferences[NAME_KEY]
            if (email != null && name != null) User(email, name, "")
            else null
        }

    suspend fun clearUser() {
        context.dataStore.edit { it.clear() }
    }
}