package co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.User
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.UserType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IPerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IUserRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class PerformerListViewModel(
    private val performerRepository: IPerformerRepository,
    private val userRepository: IUserRepository,
    private val dispatcher : CoroutineDispatcher
) : ViewModel() {
    val user: CompletableDeferred<User> = CompletableDeferred()

    private val _musicians: MutableStateFlow<List<Performer>> = MutableStateFlow(emptyList())
    val musicians = _musicians.asStateFlow().onSubscription { getMusicians() }
    private val getMusiciansStarted: AtomicBoolean = AtomicBoolean(false)

    private val _bands: MutableStateFlow<List<Performer>> = MutableStateFlow(emptyList())
    val bands = _bands.asStateFlow().onSubscription { getBands() }
    private val getBandsStarted: AtomicBoolean = AtomicBoolean(false)

    private val _favoritePerformers = MutableStateFlow<Set<Int>>(emptySet())
    val favoritePerformers = _favoritePerformers.asStateFlow().onSubscription { getFavoritePerformers() }
    private val getFavoritePerformersStarted: AtomicBoolean = AtomicBoolean(false)

    private val _updatingFavoritePerformers: MutableStateFlow<Set<Int>> = MutableStateFlow(emptySet())
    val updatingFavoritePerformers = _updatingFavoritePerformers.asStateFlow()

    private val _isRefreshing = MutableStateFlow(true)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<ErrorUiState>(ErrorUiState.NoError)
    val error = _error.asStateFlow()

    private fun getMusicians() {
        if (getMusiciansStarted.getAndSet(true))
            return // Coroutine to get musicians was already started, only start once

        viewModelScope.launch(dispatcher) {
            performerRepository.getMusicians()
                .collect { musicians ->
                    musicians
                        .onFailure {
                            _error.value = ErrorUiState.Error(R.string.network_error)
                        }
                        .onSuccess {
                            _musicians.value = it
                        }

                    _isRefreshing.value = false
                }
        }
    }

    private fun getBands() {
        if (getBandsStarted.getAndSet(true))
            return // Coroutine to get bands was already started, only start once

        viewModelScope.launch(dispatcher) {
            performerRepository.getBands()
                .collect { bands ->
                    bands
                        .onFailure {
                            _error.value = ErrorUiState.Error(R.string.network_error)
                        }
                        .onSuccess {
                            _bands.value = it
                        }

                    _isRefreshing.value = false
                }
        }
    }

    private fun getFavoritePerformers() {
        if (getFavoritePerformersStarted.getAndSet(true))
            return // Coroutine to get favorite performers was already started, only start once

        viewModelScope.launch(dispatcher) {
            val collector = user.await()

            // Visitors do not have favorite performers
            if (collector.type == UserType.Visitor)
                return@launch

            performerRepository.getFavoritePerformers(collector.id).collect { performers ->
                _favoritePerformers.value = performers.map { it.id }.toSet()
            }
        }
    }

    fun onRefreshMusicians() {
        _isRefreshing.value = true

        viewModelScope.launch(dispatcher) {
            try {
                performerRepository.refreshMusicians()
            } catch (ex: Exception) {
                _isRefreshing.value = false
                _error.value = ErrorUiState.Error(R.string.network_error)
            }
        }
    }

    fun onRefreshBands() {
        _isRefreshing.value = true

        viewModelScope.launch(dispatcher) {
            try {
                performerRepository.refreshBands()
            } catch (ex: Exception) {
                _isRefreshing.value = false
                _error.value = ErrorUiState.Error(R.string.network_error)
            }
        }
    }

    fun addFavoriteMusician(performerId: Int) {
        _updatingFavoritePerformers.value = _updatingFavoritePerformers.value + performerId

        viewModelScope.launch(dispatcher) {
            val collector = user.await()

            // Visitors do not have favorite performers
            if (collector.type == UserType.Visitor)
                return@launch

            try {
                performerRepository.addFavoriteMusician(collector.id, performerId)
            } catch (ex: Exception) {
                _error.value = ErrorUiState.Error(R.string.network_error)
            }

            _updatingFavoritePerformers.value = _updatingFavoritePerformers.value - performerId
        }
    }

    fun addFavoriteBand(performerId: Int) {
        _updatingFavoritePerformers.value = _updatingFavoritePerformers.value + performerId

        viewModelScope.launch(dispatcher) {
            val collector = user.await()

            // Visitors do not have favorite performers
            if (collector.type == UserType.Visitor)
                return@launch

            try {
                performerRepository.addFavoriteBand(collector.id, performerId)
            } catch (ex: Exception) {
                _error.value = ErrorUiState.Error(R.string.network_error)
            }

            _updatingFavoritePerformers.value = _updatingFavoritePerformers.value - performerId
        }
    }

    fun removeFavoriteMusician(performerId: Int) {
        _updatingFavoritePerformers.value = _updatingFavoritePerformers.value + performerId

        viewModelScope.launch(dispatcher) {
            val collector = user.await()

            // Visitors do not have favorite performers
            if (collector.type == UserType.Visitor)
                return@launch

            try {
                performerRepository.removeFavoriteMusician(collector.id, performerId)
            } catch (ex: Exception) {
                _error.value = ErrorUiState.Error(R.string.network_error)
            }

            _updatingFavoritePerformers.value = _updatingFavoritePerformers.value - performerId
        }
    }

    fun removeFavoriteBand(performerId: Int) {
        _updatingFavoritePerformers.value = _updatingFavoritePerformers.value + performerId

        viewModelScope.launch(dispatcher) {
            val collector = user.await()

            // Visitors do not have favorite performers
            if (collector.type == UserType.Visitor)
                return@launch

            try {
                performerRepository.removeFavoriteBand(collector.id, performerId)
            } catch (ex: Exception) {
                _error.value = ErrorUiState.Error(R.string.network_error)
            }

            _updatingFavoritePerformers.value = _updatingFavoritePerformers.value - performerId
        }
    }

    fun onErrorShown() {
        _error.value = ErrorUiState.NoError
    }

    init {
        viewModelScope.launch(dispatcher) {
            user.complete(requireNotNull(userRepository.getUser().first()))
        }
    }

    // ViewModel factory edit
    companion object {
        val KEY_PERFORMER_REPOSITORY = object : CreationExtras.Key<IPerformerRepository> {}
        val KEY_USER_REPOSITORY = object : CreationExtras.Key<IUserRepository> {}
        val KEY_DISPATCHER = object : CreationExtras.Key<CoroutineDispatcher> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PerformerListViewModel(
                    performerRepository = requireNotNull(this[KEY_PERFORMER_REPOSITORY]),
                    userRepository = requireNotNull(this[KEY_USER_REPOSITORY]),
                    dispatcher = this[KEY_DISPATCHER] ?: Dispatchers.IO
                )
            }
        }
    }
}