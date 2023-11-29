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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class BandAddMusicianViewModel(
    private val performerRepository: IPerformerRepository,
    private val performerId: Int,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _band: MutableStateFlow<Performer?> = MutableStateFlow(null)
    val band: SharedFlow<Performer?> = _band.asStateFlow().onSubscription { getBand() }
    private val getBandStarted: AtomicBoolean = AtomicBoolean(false)

    private val _memberCandidates: MutableStateFlow<List<Performer>> = MutableStateFlow(emptyList())
    val membersCandidates = _memberCandidates.asStateFlow().onSubscription { getBandMemberCandidates() }
    private val getMemberCandidatesStarted: AtomicBoolean = AtomicBoolean(false)

    private val _state = MutableStateFlow<FormUiState>(FormUiState.Input)
    val state = _state.asStateFlow()

    private val _error = MutableStateFlow<ErrorUiState>(ErrorUiState.NoError)
    val error = _error.asStateFlow()

    private fun getBand() {
        if (getBandStarted.getAndSet(true))
            return // Coroutine to get band was already started, only start once

        viewModelScope.launch(dispatcher) {
            performerRepository.getBand(performerId)
                .collect { band ->
                    if (band == null) {
                        _error.value = ErrorUiState.Error(R.string.network_error)
                    } else {
                        _band.value = band
                    }
                }
        }
    }

    private fun getBandMemberCandidates() {
        if (getMemberCandidatesStarted.getAndSet(true))
            return // Coroutine to get band member candidates was already started, only start once

        viewModelScope.launch(dispatcher) {
            performerRepository.getBandMemberCandidates()
                .collect { musicians ->
                    _memberCandidates.value = musicians
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
                BandAddMusicianViewModel(
                    performerRepository = requireNotNull(this[KEY_PERFORMER_REPOSITORY]),
                    performerId = requireNotNull(this[KEY_PERFORMER_ID]),
                    dispatcher = this[KEY_DISPATCHER] ?: Dispatchers.IO
                )
            }
        }
    }
}