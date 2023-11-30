package co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IAlbumRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlbumTrackViewModel(
    private val albumRepository: IAlbumRepository,
    private val albumId: Int,
    val dispatcher : CoroutineDispatcher
) : ViewModel() {
    private val _state = MutableStateFlow<FormUiState>(FormUiState.Input)
    val state = _state.asStateFlow()

    private val _error = MutableStateFlow<ErrorUiState>(ErrorUiState.NoError)
    val error = _error.asStateFlow()

    fun validateName(name: String): Boolean {
        return !(
            name.isEmpty() || (name.length > TRACK_NAME_MAX_LENGTH)
        )
    }

    fun validateDuration(duration: String): Boolean {
        return Regex("^([0-5]?[0-9]):([0-5][0-9])\$").matches(duration)
    }

    fun onSave(name: String, duration: String) {
        _state.value = FormUiState.Saving

        viewModelScope.launch(dispatcher) {
            try {
                delay(3000)
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

    companion object {
        const val TRACK_NAME_MAX_LENGTH = 200

        val KEY_ALBUM_REPOSITORY = object : CreationExtras.Key<IAlbumRepository> {}
        val KEY_ALBUM_ID = object : CreationExtras.Key<Int> {}
        val KEY_DISPATCHER = object : CreationExtras.Key<CoroutineDispatcher> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AlbumTrackViewModel(
                    albumRepository = requireNotNull(this[KEY_ALBUM_REPOSITORY]),
                    albumId = requireNotNull(this[KEY_ALBUM_ID]),
                    dispatcher = this[KEY_DISPATCHER] ?: Dispatchers.IO
                )
            }
        }
    }
}
