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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class AlbumViewModel(
    private val albumRepository: IAlbumRepository,
    private val albumId: Int, val dispatcher : CoroutineDispatcher) : ViewModel() {

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

    @Suppress("PropertyName")
    private val _isRefreshing = MutableStateFlow(true)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<ErrorUiState>(ErrorUiState.NoError)
    val error = _error.asStateFlow()

    private fun getAlbum() {
        if (getAlbumStarted.getAndSet(true))
            return  // Coroutine to get band was already started, only start once

        viewModelScope.launch(dispatcher) {
            albumRepository.getAlbum(albumId)
                .collect { album ->
                    if (album == null) {
                        _error.value = ErrorUiState.Error(R.string.network_error)
                    } else {
                        _album.value = album
                    }
                    _isRefreshing.value = false
                }
        }
    }

    private fun getAlbumPerformances() {
        if (getPerformerStarted.getAndSet(true))
            return  // Coroutine to get band was already started, only start once

        viewModelScope.launch(dispatcher) {
            albumRepository.getPerformers(albumId)
                .collect { performer ->
                    if (performer == null) {
                        _error.value = ErrorUiState.Error(R.string.network_error)
                    } else {
                        _albumsperformers.value = performer
                    }
                    _isRefreshing.value = false
                }
        }
    }

    private fun getTracksAlbums() {
        if (getTrackStarted.getAndSet(true))
            return  // Coroutine to get band was already started, only start once

        viewModelScope.launch(dispatcher) {
            albumRepository.getTracks(albumId)
                .collect { track ->
                    if (track == null) {
                        _error.value = ErrorUiState.Error(R.string.network_error)
                    } else {
                        _albumstracks.value = track
                    }
                    _isRefreshing.value = false
                }
        }
    }


    private fun getCommentsAlbums() {
        if (getCommentStarted.getAndSet(true))
            return // Coroutine to get band was already started, only start once

        viewModelScope.launch(dispatcher) {
            albumRepository.getComments(albumId)
                .collect { comment ->
                    if (comment == null) {
                        _error.value = ErrorUiState.Error(R.string.network_error)
                    } else {
                        _albumscomments.value = comment
                    }
                    _isRefreshing.value = false
                }
        }
    }


    fun onRefresh() {
        _isRefreshing.value = true
        viewModelScope.launch(dispatcher) {
            try {
                albumRepository.refreshAlbum(albumId)
            } catch (ex: Exception) {
                _isRefreshing.value = false
                _error.value = ErrorUiState.Error(R.string.network_error)
            }
        }
    }

    fun onErrorShown() {
        _error.value = ErrorUiState.NoError
    }
    companion object {
        val KEY_ALBUM_REPOSITORY = object : CreationExtras.Key<IAlbumRepository> {}
        val KEY_ALBUM_ID = object : CreationExtras.Key<Int> {}
        val KEY_DISPATCHER = object : CreationExtras.Key<CoroutineDispatcher> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AlbumViewModel(
                    albumRepository = requireNotNull(this[KEY_ALBUM_REPOSITORY]),
                    albumId = requireNotNull(this[KEY_ALBUM_ID]),
                    dispatcher = this[KEY_DISPATCHER] ?: Dispatchers.IO

                )
            }
        }
    }
}
