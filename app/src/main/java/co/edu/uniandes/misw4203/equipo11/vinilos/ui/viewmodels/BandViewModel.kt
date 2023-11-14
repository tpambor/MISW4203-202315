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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class BandViewModel(
    private val performerRepository: IPerformerRepository,
    private val performerId: Int
) : ViewModel() {
    private val _band: MutableStateFlow<Performer?> = MutableStateFlow(null)
    val band = _band.asStateFlow().onSubscription { getBand() }
    private val getBandStarted: AtomicBoolean = AtomicBoolean(false)

    private val _albums: MutableStateFlow<List<Album>> = MutableStateFlow(emptyList())
    val albums = _albums.asStateFlow().onSubscription { getAlbums() }
    private val getAlbumsStarted: AtomicBoolean = AtomicBoolean(false)

    private val _isRefreshing = MutableStateFlow(true)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<ErrorUiState>(ErrorUiState.NoError)
    val error = _error.asStateFlow()

    private fun getBand() {
        if (getBandStarted.getAndSet(true))
            return // Coroutine to get band was already started, only start once

        viewModelScope.launch {
            performerRepository.getBand(performerId)
                .collect { band ->
                    if (band == null) {
                        _error.value = ErrorUiState.Error(R.string.network_error)
                    } else {
                        _band.value = band
                        _error.value = ErrorUiState.NoError
                    }
                    _isRefreshing.value = false
                }
        }
    }

    private fun getAlbums() {
        if (getAlbumsStarted.getAndSet(true))
            return // Coroutine to get albums was already started, only start once

        viewModelScope.launch {
            performerRepository.getAlbums(performerId)
                .collect { albums ->
                    _albums.value = albums
                    _error.value = ErrorUiState.NoError
                    _isRefreshing.value = false
                }
        }
    }

    // ViewModel factory
    companion object {
        val KEY_PERFORMER_REPOSITORY = object : CreationExtras.Key<IPerformerRepository> {}
        val KEY_PERFORMER_ID = object : CreationExtras.Key<Int> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                BandViewModel(
                    performerRepository = requireNotNull(this[KEY_PERFORMER_REPOSITORY]),
                    performerId = requireNotNull(this[KEY_PERFORMER_ID])
                )
            }
        }
    }
}
