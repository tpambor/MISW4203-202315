package co.edu.uniandes.misw4203.equipo11.vinilos

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import co.edu.uniandes.misw4203.equipo11.vinilos.models.User
import co.edu.uniandes.misw4203.equipo11.vinilos.models.UserType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class PreferenceDataStore private constructor(
    @Suppress("SameParameterValue") private val dataStore: DataStore<Preferences>
) {
    private val _userType = stringPreferencesKey("user_type")

    fun getUser(): Flow<User?> {
        return dataStore.data.map { preferences ->
            val type = preferences[_userType] ?: return@map null

            User(UserType.valueOf(type))
        }
    }

    suspend fun setUser(user: User) {
        dataStore.edit { settings ->
            settings[_userType] = user.type.toString()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: PreferenceDataStore? = null

        fun create(context: Context) {
            synchronized(this) {
                INSTANCE = PreferenceDataStore(context.dataStore)
            }
        }

        fun getInstance(): PreferenceDataStore {
            synchronized(this) {
                return INSTANCE ?: throw IllegalStateException()
            }
        }
    }
}
