package co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IPerformerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class MusicianViewModel(
    private val performerRepository: IPerformerRepository,
    private val performerId: Int
) : ViewModel() {
    private val _musician: MutableStateFlow<Performer?> = MutableStateFlow(null)
    val musician = _musician.asStateFlow().onSubscription { getMusician() }
    private val getMusicianStarted: AtomicBoolean = AtomicBoolean(false)

    private val _isRefreshing = MutableStateFlow(true)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<ErrorUiState>(ErrorUiState.NoError)
    val error = _error.asStateFlow()

    private fun getMusician() {
        if (getMusicianStarted.getAndSet(true))
            return // Coroutine to get musicians was already started, only start once

        viewModelScope.launch {
            performerRepository.getMusician(performerId)
                .collect { musician ->
                    if (musician == null) {
                        _error.value = ErrorUiState.Error(R.string.network_error)
                    } else {
                        _musician.value = musician
                        _error.value = ErrorUiState.NoError
                    }
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
                MusicianViewModel(
                    performerRepository = requireNotNull(this[KEY_PERFORMER_REPOSITORY]),
                    performerId = requireNotNull(this[KEY_PERFORMER_ID])
                )
            }
        }
    }
}
