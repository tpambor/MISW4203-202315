package co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.repositories.IAlbumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Immutable
sealed interface ErrorUiState {
    data class Error(@StringRes val resourceId: Int) : ErrorUiState
    object NoError : ErrorUiState
}

data class AlbumListErrorUiState(
    val errorState: ErrorUiState
)

class AlbumListViewModel(val albumRepository: IAlbumRepository) : ViewModel() {
    private val _albums: MutableStateFlow<List<Album>> = MutableStateFlow(emptyList())
    val albums = _albums.asStateFlow()

    private val _isRefreshing = MutableStateFlow(true)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow(AlbumListErrorUiState(ErrorUiState.NoError))
    val error = _error.asStateFlow()

    init {
        viewModelScope.launch {
            albumRepository.getAlbums()
                .collect { albums ->
                    if (albums == null) {
                        _error.value = AlbumListErrorUiState(ErrorUiState.Error(R.string.network_error))
                    } else {
                        _albums.value = albums
                        _error.value = AlbumListErrorUiState(ErrorUiState.NoError)
                    }
                    _isRefreshing.value = false
                }
        }
    }

    fun onRefresh() {
        _isRefreshing.value = true
        _error.value = AlbumListErrorUiState(ErrorUiState.NoError)

        viewModelScope.launch {
            albumRepository.refresh()
        }
    }

    // ViewModel factory
    companion object {
        val KEY_ALBUM_REPOSITORY = object : CreationExtras.Key<IAlbumRepository> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AlbumListViewModel(
                    albumRepository = requireNotNull(this[KEY_ALBUM_REPOSITORY]),
                )
            }
        }
    }
}
