package co.edu.uniandes.misw4203.equipo11.vinilos.repositories

import co.edu.uniandes.misw4203.equipo11.vinilos.PreferenceDataStore
import co.edu.uniandes.misw4203.equipo11.vinilos.models.User
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    fun getUser(): Flow<User?>
    suspend fun setUser(user: User)

}
class UserRepository: IUserRepository {
    private val userPreferences = PreferenceDataStore.getInstance()

    override fun getUser(): Flow<User?> {
        return userPreferences.getUser()
    }

    override suspend fun setUser(user: User) {
        userPreferences.setUser(user)
    }
}
