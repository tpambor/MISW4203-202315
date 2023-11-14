package co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Comment
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Track
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IAlbumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class AlbumViewModel(
    private val albumRepository: IAlbumRepository,
    private val albumId: Int) : ViewModel() {
    private val _album: MutableStateFlow<Album?> = MutableStateFlow(null)
    val album = _album.asStateFlow().onSubscription { getAlbum() }
    private val getAlbumStarted: AtomicBoolean = AtomicBoolean(false)

    private val _albumsperformers: MutableStateFlow<List<Performer>> = MutableStateFlow(emptyList())
    val albumsperformers = _albumsperformers.asStateFlow().onSubscription { getAlbumPerformances() }
    private val getPerformerStarted: AtomicBoolean = AtomicBoolean(false)

    private val _albumstracks: MutableStateFlow<List<Track>> = MutableStateFlow(emptyList())
    val albumstracks = _albumstracks.asStateFlow().onSubscription { getTracksAlbums() }
    private val getTrackStarted: AtomicBoolean = AtomicBoolean(false)

    private val _albumscomments: MutableStateFlow<List<Comment>> = MutableStateFlow(emptyList())
    val albumscomments = _albumscomments.asStateFlow().onSubscription { getCommentsAlbums() }
    private val getCommentStarted: AtomicBoolean = AtomicBoolean(false)

    private val _isRefreshing = MutableStateFlow(true)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<ErrorUiState>(ErrorUiState.NoError)
    val error = _error.asStateFlow()

    private fun getAlbum() {
        if (getAlbumStarted.getAndSet(true))
            return // Coroutine to get musicians was already started, only start once

        viewModelScope.launch {
            albumRepository.getAlbum(albumId)
                .collect { album ->
                    if (album == null) {
                        _error.value = ErrorUiState.Error(R.string.network_error)
                    } else {
                        _album.value = album
                        _error.value = ErrorUiState.NoError
                    }
                    _isRefreshing.value = false
                }
        }
    }

    private fun getAlbumPerformances() {
        if (getPerformerStarted.getAndSet(true))
            return // Coroutine to get musicians was already started, only start once

        viewModelScope.launch {
            albumRepository.getPerformanceAlbums(albumId)
                .collect {  performer ->
                    _albumsperformers.value = performer
                    _error.value = ErrorUiState.NoError
                    _isRefreshing.value = false
                }
        }
    }

    private fun getTracksAlbums() {
        if (getTrackStarted.getAndSet(true))
            return // Coroutine to get musicians was already started, only start once

        viewModelScope.launch {
            albumRepository.getTracksAlbums(albumId)
                .collect {  track ->
                    _albumstracks.value = track
                    _error.value = ErrorUiState.NoError
                    _isRefreshing.value = false
                }
        }
    }

    private fun getCommentsAlbums() {
        if (getTrackStarted.getAndSet(true))
            return // Coroutine to get musicians was already started, only start once

        viewModelScope.launch {
            albumRepository.getCommentsAlbums(albumId)
                .collect {  comment ->
                    _albumscomments.value = comment
                    _error.value = ErrorUiState.NoError
                    _isRefreshing.value = false
                }
        }
    }

    // ViewModel factory
    companion object {
        val KEY_ALBUM_REPOSITORY = object : CreationExtras.Key<IAlbumRepository> {}
        val KEY_ALBUM_ID = object : CreationExtras.Key<Int> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AlbumViewModel(
                    albumRepository = requireNotNull(this[KEY_ALBUM_REPOSITORY]),
                    albumId = requireNotNull(this[KEY_ALBUM_ID])
                )
            }
        }
    }
}
