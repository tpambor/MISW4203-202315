package co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumRequestJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IAlbumRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlbumCreateViewModel(
    private val albumRepository: IAlbumRepository,
    val dispatcher : CoroutineDispatcher
) : ViewModel() {
    private val _formState = MutableStateFlow<FormUiState>(FormUiState.Input)
    val formState = _formState.asStateFlow()

    private val _error = MutableStateFlow<ErrorUiState>(ErrorUiState.NoError)
    val error = _error.asStateFlow()

    fun insertAlbum(album: AlbumRequestJson) {
        _formState.value = FormUiState.Saving

        viewModelScope.launch(dispatcher) {
            try {
                albumRepository.insertAlbum(album)
            } catch (ex: Exception) {
                _error.value = ErrorUiState.Error(R.string.network_error)
                _formState.value = FormUiState.Input
                return@launch
            }

            _formState.value = FormUiState.Saved
        }
    }

    fun onErrorShown() {
        _error.value = ErrorUiState.NoError
    }

    companion object {
        const val NAME_MAX_LENGTH = 200
        const val DESCRIPTION_MAX_LENGTH = 2000

        val KEY_ALBUM_REPOSITORY = object : CreationExtras.Key<IAlbumRepository> {}
        val KEY_DISPATCHER = object : CreationExtras.Key<CoroutineDispatcher> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AlbumCreateViewModel(
                    albumRepository = requireNotNull(this[KEY_ALBUM_REPOSITORY]),
                    dispatcher = this[KEY_DISPATCHER] ?: Dispatchers.IO
                )
            }
        }
    }
}
