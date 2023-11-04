package co.edu.uniandes.misw4203.equipo11.vinilos

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import co.edu.uniandes.misw4203.equipo11.vinilos.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")
class PreferenceDataStore(private val context: Context) {

    private val _userType = stringPreferencesKey("user_type")

    fun getUser(): Flow<User?> {
        return context.dataStore.data.map { preferences ->
            User(preferences[_userType] ?: "")
        }
    }

    suspend fun setUser(user: User) {
        context.dataStore.edit { settings ->
            settings[_userType] = user.type
        }
    }
}