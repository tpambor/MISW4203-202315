package co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories

import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.PreferenceDataStore
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.User
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.UserType
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    fun getUser(): Flow<User?>
    suspend fun login(userType: UserType)
}

class UserRepository: IUserRepository {
    private val userPreferences = PreferenceDataStore.getInstance()

    override fun getUser(): Flow<User?> {
        return userPreferences.getUser()
    }

    override suspend fun login(userType: UserType) {
        userPreferences.setUser(User(userType, 1))
    }
}
