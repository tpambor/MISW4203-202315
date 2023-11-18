package co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.User
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.UserType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IUserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class UserViewModel(
    private val userRepository: IUserRepository,
    private val dispatcher : CoroutineDispatcher
) : ViewModel() {
    @Immutable
    sealed interface LoginUiState {
        data object NotLoggedIn : LoginUiState
        data object LoggedIn : LoginUiState
    }

    private val _status = MutableStateFlow<LoginUiState>(LoginUiState.NotLoggedIn)
    val status = _status.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow().onSubscription { getUser() }
    private val getUserStarted: AtomicBoolean = AtomicBoolean(false)

    private fun getUser() {
        if (getUserStarted.getAndSet(true))
            return // Coroutine to get user was already started, only start once

        viewModelScope.launch(dispatcher) {
            userRepository.getUser().collect { user ->
                _user.value = user
            }
        }
    }

    fun onLogin(userType: UserType) {
        viewModelScope.launch {
            userRepository.login(userType)
            _status.value = LoginUiState.LoggedIn
        }
    }

    companion object {
        val KEY_USER_REPOSITORY = object : CreationExtras.Key<IUserRepository> {}
        val KEY_DISPATCHER = object : CreationExtras.Key<CoroutineDispatcher> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UserViewModel(
                    userRepository = requireNotNull(this[KEY_USER_REPOSITORY]),
                    dispatcher = this[KEY_DISPATCHER] ?: Dispatchers.IO
                )
            }
        }
    }
}
