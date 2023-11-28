package co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels

import androidx.compose.runtime.Immutable

@Immutable
sealed interface FormUiState {
    data object Input : FormUiState
    data object Saving : FormUiState
    data object Saved : FormUiState
}
