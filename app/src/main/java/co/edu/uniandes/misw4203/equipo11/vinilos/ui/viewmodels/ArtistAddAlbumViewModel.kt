package co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IPerformerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class ArtistAddAlbumViewModel(
    private val performerRepository: IPerformerRepository,
    private val performerId: Int,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _performer: MutableStateFlow<Performer?> = MutableStateFlow(null)
    val performer: SharedFlow<Performer?> = _performer.asStateFlow().onSubscription { getPerformer() }
    private val getPerformerStarted: AtomicBoolean = AtomicBoolean(false)

    private val _albums: MutableStateFlow<List<Album>> = MutableStateFlow(emptyList())
    val albums = _albums.asStateFlow().onSubscription { getAlbums() }
    private val getAlbumsStarted: AtomicBoolean = AtomicBoolean(false)

    private val _state = MutableStateFlow<FormUiState>(FormUiState.Input)
    val state = _state.asStateFlow()

    private val _error = MutableStateFlow<ErrorUiState>(ErrorUiState.NoError)
    val error = _error.asStateFlow()

    private fun getPerformer() {
        if (getPerformerStarted.getAndSet(true))
            return // Coroutine to get performer was already started, only start once

        viewModelScope.launch(dispatcher) {
            performerRepository.getPerformer(performerId)
                .collect { performer ->
                    if (performer == null) {
                        _error.value = ErrorUiState.Error(R.string.network_error)
                    } else {
                        _performer.value = performer
                    }
                }
        }
    }

    private fun getAlbums() {
        if (getAlbumsStarted.getAndSet(true))
            return // Coroutine to get albums was already started, only start once

        viewModelScope.launch(dispatcher) {
            performerRepository.getAlbumCandidates(performerId)
                .collect { albums ->
                    _albums.value = albums
                }
        }
    }

    fun onSave(musicianId: Int) {
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
        val KEY_PERFORMER_REPOSITORY = object : CreationExtras.Key<IPerformerRepository> {}
        val KEY_PERFORMER_ID = object : CreationExtras.Key<Int> {}
        val KEY_DISPATCHER = object : CreationExtras.Key<CoroutineDispatcher> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ArtistAddAlbumViewModel(
                    performerRepository = requireNotNull(this[KEY_PERFORMER_REPOSITORY]),
                    performerId = requireNotNull(this[KEY_PERFORMER_ID]),
                    dispatcher = this[KEY_DISPATCHER] ?: Dispatchers.IO
                )
            }
        }
    }
}
