package co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorWithPerformers
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.ICollectorRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class CollectorListViewModel(
    private val collectorRepository: ICollectorRepository,
    private val dispatcher : CoroutineDispatcher
) : ViewModel() {
    private val _collectors: MutableStateFlow<List<CollectorWithPerformers>> = MutableStateFlow(emptyList())
    val collectors = _collectors.asStateFlow().onSubscription { getCollectors() }
    private val getCollectorsStarted: AtomicBoolean = AtomicBoolean(false)

    private val _isRefreshing = MutableStateFlow(true)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<ErrorUiState>(ErrorUiState.NoError)
    val error = _error.asStateFlow()

    private fun getCollectors() {
        if (getCollectorsStarted.getAndSet(true))
            return // Coroutine to get collectors was already started, only start once

        viewModelScope.launch(dispatcher) {
            var needsRefresh = collectorRepository.needsRefresh()

            collectorRepository.getCollectorsWithFavoritePerformers()
                .collect { collectors ->
                    _collectors.value = collectors
                    _error.value = ErrorUiState.NoError

                    if (needsRefresh) {
                        onRefresh()
                        needsRefresh = false
                    }
                    else {
                        _isRefreshing.value = false
                    }
                }
        }
    }

    fun onRefresh() {
        _isRefreshing.value = true
        _error.value = ErrorUiState.NoError

        viewModelScope.launch(dispatcher) {
            try {
                collectorRepository.refresh()
            } catch (ex: Exception) {
                _isRefreshing.value = false
                _error.value = ErrorUiState.Error(R.string.network_error)
            }
        }
    }

    // ViewModel factory
    companion object {
        val KEY_COLLECTOR_REPOSITORY = object : CreationExtras.Key<ICollectorRepository> {}
        val KEY_DISPATCHER = object : CreationExtras.Key<CoroutineDispatcher> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CollectorListViewModel(
                    collectorRepository = requireNotNull(this[KEY_COLLECTOR_REPOSITORY]),
                    dispatcher = this[KEY_DISPATCHER] ?: Dispatchers.IO
                )
            }
        }
    }
}
