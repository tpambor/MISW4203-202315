package co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
sealed interface ErrorUiState {
    data class Error(@StringRes val resourceId: Int) : ErrorUiState
    object NoError : ErrorUiState
}
