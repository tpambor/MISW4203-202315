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
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumRequestJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IAlbumRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class AlbumViewModel(
    private val albumRepository: IAlbumRepository,
    private val albumId: Int?,
    val dispatcher : CoroutineDispatcher
) : ViewModel() {
    private val _album: MutableStateFlow<Album?> = MutableStateFlow(null)
    val album = _album.asStateFlow().onSubscription { getAlbum() }
    private val getAlbumStarted: AtomicBoolean = AtomicBoolean(false)

    private val _performers: MutableStateFlow<List<Performer>> = MutableStateFlow(emptyList())
    val performers = _performers.asStateFlow().onSubscription { getPerformers() }
    private val getPerformersStarted: AtomicBoolean = AtomicBoolean(false)

    private val _tracks: MutableStateFlow<List<Track>> = MutableStateFlow(emptyList())
    val tracks = _tracks.asStateFlow().onSubscription { getTracks() }
    private val getTracksStarted: AtomicBoolean = AtomicBoolean(false)

    private val _comments: MutableStateFlow<List<Comment>> = MutableStateFlow(emptyList())
    val comments = _comments.asStateFlow().onSubscription { getComments() }
    private val getCommentsStarted: AtomicBoolean = AtomicBoolean(false)

    private val _isRefreshing = MutableStateFlow(true)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<ErrorUiState>(ErrorUiState.NoError)
    val error = _error.asStateFlow()

    private val _formState = MutableStateFlow<FormUiState>(FormUiState.Input)
    val formState = _formState.asStateFlow()

    private val _insertAlbumResult = MutableStateFlow<Result<Unit>>(Result.success(Unit))
    val insertAlbumResult: StateFlow<Result<Unit>> get() = _insertAlbumResult

    fun insertAlbum(album: AlbumRequestJson) {
        _formState.value = FormUiState.Saving
        viewModelScope.launch {
            try {
                albumRepository.insertAlbum(album)
                _insertAlbumResult.value = Result.success(Unit)
                _formState.value = FormUiState.Saved
            } catch (ex: Exception) {
                _insertAlbumResult.value = Result.failure(ex)
                _formState.value = FormUiState.Input
            }
        }
    }


    private fun getAlbum() {
        if (getAlbumStarted.getAndSet(true))
            return  // Coroutine to get album was already started, only start once

        viewModelScope.launch(dispatcher) {
            if (albumId != null) {
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
    }

    private fun getPerformers() {
        if (getPerformersStarted.getAndSet(true))
            return  // Coroutine to get performers was already started, only start once

        viewModelScope.launch(dispatcher) {
            if (albumId != null) {
                albumRepository.getPerformers(albumId)
                    .collect { performers ->
                        _performers.value = performers
                        _isRefreshing.value = false
                    }
            }
        }
    }

    private fun getTracks() {
        if (getTracksStarted.getAndSet(true))
            return  // Coroutine to get tracks was already started, only start once

        viewModelScope.launch(dispatcher) {
            if (albumId != null) {
                albumRepository.getTracks(albumId)
                    .collect { tracks ->
                        _tracks.value = tracks
                        _isRefreshing.value = false
                    }
            }
        }
    }


    private fun getComments() {
        if (getCommentsStarted.getAndSet(true))
            return // Coroutine to get comments was already started, only start once

        viewModelScope.launch(dispatcher) {
            if (albumId != null) {
                albumRepository.getComments(albumId)
                    .collect { comment ->
                        _comments.value = comment
                        _isRefreshing.value = false
                    }
            }
        }
    }


    fun onRefresh() {
        _isRefreshing.value = true

        viewModelScope.launch(dispatcher) {
            try {
                if (albumId != null) {
                    albumRepository.refreshAlbum(albumId)
                }
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
        const val NAME_MAX_LENGTH = 200
        const val DESCRIPTION_MAX_LENGTH = 2000

        val KEY_ALBUM_REPOSITORY = object : CreationExtras.Key<IAlbumRepository> {}
        val KEY_ALBUM_ID = object : CreationExtras.Key<Int> {}
        val KEY_DISPATCHER = object : CreationExtras.Key<CoroutineDispatcher> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AlbumViewModel(
                    albumRepository = requireNotNull(this[KEY_ALBUM_REPOSITORY]),
                    albumId = this[KEY_ALBUM_ID],
                    dispatcher = this[KEY_DISPATCHER] ?: Dispatchers.IO
                )
            }
        }
    }
}
