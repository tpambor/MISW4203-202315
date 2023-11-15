package co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IPerformerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class MusicianViewModel(
    performerRepository: IPerformerRepository,
    performerId: Int
) : PerformerViewModel(performerRepository, performerId) {
    override val performerType: PerformerType = PerformerType.MUSICIAN

    private val _musician: MutableStateFlow<Performer?> = MutableStateFlow(null)
    override val performer: SharedFlow<Performer?> = _musician.asStateFlow().onSubscription { getMusician() }
    private val getMusicianStarted: AtomicBoolean = AtomicBoolean(false)

    private fun getMusician() {
        if (getMusicianStarted.getAndSet(true))
            return // Coroutine to get musician was already started, only start once

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
