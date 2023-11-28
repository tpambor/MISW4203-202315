package co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.User
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.UserType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IAlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IUserRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AlbumCommentViewModel(
    private val albumRepository: IAlbumRepository,
    private val albumId: Int,
    private val userRepository: IUserRepository,
    val dispatcher : CoroutineDispatcher
) : ViewModel() {
    val user: CompletableDeferred<User> = CompletableDeferred()

    private val _state = MutableStateFlow<FormUiState>(FormUiState.Input)
    val state = _state.asStateFlow()

    private val _error = MutableStateFlow<ErrorUiState>(ErrorUiState.NoError)
    val error = _error.asStateFlow()

    fun validateComment(comment: String): Boolean {
        return comment.isEmpty() || (comment.length > COMMENT_MAX_LENGTH)
    }

    fun onSave(rating: Int, comment: String) {
        _state.value = FormUiState.Saving

        viewModelScope.launch(dispatcher) {
            val collector = user.await()

            // Visitors cannot create comments
            if (collector.type == UserType.Visitor)
                return@launch

            try {
                albumRepository.addComment(albumId, collector.id, rating, comment)
            } catch (ex: Exception) {
                _error.value = ErrorUiState.Error(R.string.network_error)
                _state.value = FormUiState.Input
                return@launch
            }

            _state.value = FormUiState.Saved
        }
    }

    fun onErrorShown() {
        _error.value = ErrorUiState.NoError
    }

    init {
        viewModelScope.launch(dispatcher) {
            user.complete(requireNotNull(userRepository.getUser().first()))
        }
    }

    companion object {
        const val COMMENT_MAX_LENGTH = 2000

        val KEY_ALBUM_REPOSITORY = object : CreationExtras.Key<IAlbumRepository> {}
        val KEY_ALBUM_ID = object : CreationExtras.Key<Int> {}
        val KEY_USER_REPOSITORY = object : CreationExtras.Key<IUserRepository> {}
        val KEY_DISPATCHER = object : CreationExtras.Key<CoroutineDispatcher> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AlbumCommentViewModel(
                    albumRepository = requireNotNull(this[KEY_ALBUM_REPOSITORY]),
                    albumId = requireNotNull(this[KEY_ALBUM_ID]),
                    userRepository = requireNotNull(this[KEY_USER_REPOSITORY]),
                    dispatcher = this[KEY_DISPATCHER] ?: Dispatchers.IO
                )
            }
        }
    }
}
