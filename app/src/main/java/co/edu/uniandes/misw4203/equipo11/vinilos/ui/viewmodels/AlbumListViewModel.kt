package co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IAlbumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class AlbumListViewModel(val albumRepository: IAlbumRepository) : ViewModel() {
    private val _albums: MutableStateFlow<List<Album>> = MutableStateFlow(emptyList())
    val albums = _albums.asStateFlow().onSubscription { getAlbums() }
    private val getAlbumsStarted: AtomicBoolean = AtomicBoolean(false)

    private val _isRefreshing = MutableStateFlow(true)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<ErrorUiState>(ErrorUiState.NoError)
    val error = _error.asStateFlow()


    private fun getAlbums() {
        if (getAlbumsStarted.getAndSet(true))
            return // Coroutine to get albums was already started, only start once

        viewModelScope.launch {
            albumRepository.getAlbums()
                .collect { albums ->
                    if (albums == null) {
                        _error.value = ErrorUiState.Error(R.string.network_error)
                    } else {
                        _albums.value = albums
                        _error.value = ErrorUiState.NoError
                    }
                    _isRefreshing.value = false
                }
        }
    }

    fun onRefresh() {
        _isRefreshing.value = true
        _error.value = ErrorUiState.NoError

        viewModelScope.launch {
            if (!albumRepository.refresh()) {
                _isRefreshing.value = false
                _error.value = ErrorUiState.Error(R.string.network_error)
            }
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
