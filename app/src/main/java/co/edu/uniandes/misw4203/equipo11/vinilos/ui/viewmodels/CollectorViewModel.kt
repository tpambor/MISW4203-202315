package co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Collector
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorAlbum
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IAlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.ICollectorRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class CollectorViewModel(
    private val collectorRepository: ICollectorRepository,
    private val collectorId: Int,
    val dispatcher : CoroutineDispatcher
) : ViewModel() {
    private val _collector: MutableStateFlow<Collector?> = MutableStateFlow(null)
    val collector = _collector.asStateFlow().onSubscription { getCollector() }
    private val getCollectorStarted: AtomicBoolean = AtomicBoolean(false)

    private val _favoritePerformers: MutableStateFlow<List<Performer>> = MutableStateFlow(emptyList())
    val favoritePerformers = _favoritePerformers.asStateFlow().onSubscription { getFavoritePerformers() }
    private val getFavoritePerformersStarted: AtomicBoolean = AtomicBoolean(false)

    private val _albums: MutableStateFlow<List<CollectorAlbum>> = MutableStateFlow(emptyList())
    val albums = _albums.asStateFlow().onSubscription { getAlbums() }
    private val getAlbumsStarted: AtomicBoolean = AtomicBoolean(false)


    private val _isRefreshing = MutableStateFlow(true)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<ErrorUiState>(ErrorUiState.NoError)
    val error = _error.asStateFlow()

    private fun getCollector() {
        if (getCollectorStarted.getAndSet(true))
            return  // Coroutine to get collector was already started, only start once

        viewModelScope.launch(dispatcher) {
            collectorRepository.getCollector(collectorId)
                .collect { collector ->
                    if (collector == null) {
                        _error.value = ErrorUiState.Error(R.string.network_error)
                    } else {
                        _collector.value = collector
                    }
                    _isRefreshing.value = false
                }
        }
    }

    private fun getFavoritePerformers() {
        if (getFavoritePerformersStarted.getAndSet(true))
            return  // Coroutine to get favorite performers was already started, only start once

        viewModelScope.launch(dispatcher) {
            collectorRepository.getFavoritePerformers(collectorId)
                .collect { performers ->
                    _favoritePerformers.value = performers
                    _isRefreshing.value = false
                }
        }
    }

    private fun getAlbums() {
        if (getAlbumsStarted.getAndSet(true))
            return  // Coroutine to get albums was already started, only start once

        viewModelScope.launch(dispatcher) {
            collectorRepository.getAlbums(collectorId)
                .collect { albums ->
                    _albums.value = albums
                    _isRefreshing.value = false
                }
        }
    }

    fun onRefresh() {
        _isRefreshing.value = true

        viewModelScope.launch(dispatcher) {
            try {
                collectorRepository.refreshCollector(collectorId)
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
        val KEY_COLLECTOR_REPOSITORY = object : CreationExtras.Key<ICollectorRepository> {}
        val KEY_COLLECTOR_ID = object : CreationExtras.Key<Int> {}
        val KEY_DISPATCHER = object : CreationExtras.Key<CoroutineDispatcher> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CollectorViewModel(
                    collectorRepository = requireNotNull(this[KEY_COLLECTOR_REPOSITORY]),
                    collectorId = requireNotNull(this[KEY_COLLECTOR_ID]),
                    dispatcher = this[KEY_DISPATCHER] ?: Dispatchers.IO
                )
            }
        }
    }
}
