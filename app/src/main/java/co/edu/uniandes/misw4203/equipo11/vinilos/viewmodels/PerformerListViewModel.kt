package co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Band
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Musician
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.repositories.IAlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.repositories.IPerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.repositories.PerformerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerformerListViewModel(val performerRepository: IPerformerRepository) : ViewModel() {
    private val _musicians: MutableStateFlow<List<Musician>> = MutableStateFlow(emptyList())
    val musicians = _musicians.asStateFlow()

    private val _bands: MutableStateFlow<List<Band>> = MutableStateFlow(emptyList())
    val bands = _bands.asStateFlow()

    private val _isRefreshing = MutableStateFlow(true)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<ErrorUiState>(ErrorUiState.NoError)
    val error = _error.asStateFlow()

    init {
        viewModelScope.launch {
            performerRepository.getMusicians()
                .collect { musicians ->
                    if (musicians == null) {
                        _error.value = ErrorUiState.Error(R.string.network_error)
                    } else {
                        _musicians.value = musicians
                        _error.value = ErrorUiState.NoError
                    }
                    _isRefreshing.value = false
                }
        }
    }

    fun getMusicians(){
        viewModelScope.launch {
            performerRepository.getMusicians()
                .collect { musicians ->
                    if (musicians == null) {
                        _error.value = ErrorUiState.Error(R.string.network_error)
                    } else {
                        _musicians.value = musicians
                        _error.value = ErrorUiState.NoError
                    }
                    _isRefreshing.value = false
                }
        }
    }

    fun getBands(){
        viewModelScope.launch {
            performerRepository.getBands()
                .collect { bands ->
                    if (bands == null) {
                        _error.value = ErrorUiState.Error(R.string.network_error)
                    } else {
                        _bands.value = bands
                        _error.value = ErrorUiState.NoError
                    }
                    _isRefreshing.value = false
                }
        }
    }

    fun onRefreshMusicians() {
        _isRefreshing.value = true
        _error.value = ErrorUiState.NoError

        viewModelScope.launch {
            if (!performerRepository.refreshMusicians()) {
                _isRefreshing.value = false
                _error.value = ErrorUiState.Error(R.string.network_error)
            }
        }
    }

    fun onRefreshBands() {
        _isRefreshing.value = true
        _error.value = ErrorUiState.NoError

        viewModelScope.launch {
            if (!performerRepository.refreshBands()) {
                _isRefreshing.value = false
                _error.value = ErrorUiState.Error(R.string.network_error)
            }
        }
    }

    // ViewModel factory
    companion object {
        val KEY_PERFORMER_REPOSITORY = object : CreationExtras.Key<IPerformerRepository> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PerformerListViewModel(
                    performerRepository = requireNotNull(this[KEY_PERFORMER_REPOSITORY]),
                )
            }
        }
    }
}