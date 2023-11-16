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

class BandViewModel(
    performerRepository: IPerformerRepository,
    performerId: Int
) : PerformerViewModel(performerRepository, performerId) {
    override val performerType: PerformerType = PerformerType.BAND

    private val _band: MutableStateFlow<Performer?> = MutableStateFlow(null)
    override val performer: SharedFlow<Performer?> = _band.asStateFlow().onSubscription { getBand() }
    private val getBandStarted: AtomicBoolean = AtomicBoolean(false)

    private val _members: MutableStateFlow<List<Performer>> = MutableStateFlow(emptyList())
    val members = _members.asStateFlow().onSubscription { getBandMembers() }
    private val getMembersStarted: AtomicBoolean = AtomicBoolean(false)

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

    private fun getBandMembers() {
        if (getMembersStarted.getAndSet(true))
            return // Coroutine to get band members was already started, only start once

        viewModelScope.launch {
            performerRepository.getBandMembers(performerId)
                .collect { musicians ->
                    _members.value = musicians
                    _error.value = ErrorUiState.NoError
                    _isRefreshing.value = false
                }
        }
    }

    override fun onRefresh() {
        _isRefreshing.value = true

        viewModelScope.launch {
            try {
                performerRepository.refreshBand(performerId)
            } catch (ex: Exception) {
                _isRefreshing.value = false
                _error.value = ErrorUiState.Error(R.string.network_error)
            }
        }
    }

    // ViewModel factory
    companion object {
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
