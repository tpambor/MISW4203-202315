package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.AlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.AlbumTrackViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.FormUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AlbumTrackScreen(snackbarHostState: SnackbarHostState, albumId: Int, navController: NavHostController, activityScope: CoroutineScope) {
    val viewModel: AlbumTrackViewModel = viewModel(
        factory = AlbumTrackViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(AlbumTrackViewModel.KEY_ALBUM_REPOSITORY, AlbumRepository())
            set(AlbumTrackViewModel.KEY_ALBUM_ID, albumId)
        }
    )

    val state by viewModel.state.collectAsStateWithLifecycle(
        FormUiState.Input
    )
    val error by viewModel.error.collectAsStateWithLifecycle(
        ErrorUiState.NoError
    )

    if (state == FormUiState.Saved) {
        LaunchedEffect(state) {
            activityScope.launch {
                snackbarHostState.showSnackbar("Track agregado exitosamente")
            }
            navController.navigate("albums/$albumId") {
                popUpTo("albums/$albumId")

                launchSingleTop = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        AlbumTrack(
            state = state,
            focusManager = LocalFocusManager.current,
            validateName = viewModel::validateName,
            validateDuration = viewModel::validateDuration,
            onSave = viewModel::onSave
        )
    }

    if (error is ErrorUiState.Error) {
        val message = stringResource((error as ErrorUiState.Error).resourceId)
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
            viewModel.onErrorShown()
        }
    }
}

@SuppressWarnings("kotlin:S3776") // Exception: A larger than usual number of ifs is necessary for input validation. Exception here as code complexity is only slightly increased by them
@Composable
private fun AlbumTrack(
    state: FormUiState,
    focusManager: FocusManager,
    validateName: (String) -> Boolean,
    validateDuration: (String) -> Boolean,
    onSave: (String, String) -> Unit,
) {
    val formEnabled = state == FormUiState.Input

    var name by rememberSaveable { mutableStateOf("") }
    var nameChanged by rememberSaveable { mutableStateOf(false) }
    var nameError by rememberSaveable { mutableStateOf(!validateName(name)) }

    var duration by rememberSaveable { mutableStateOf("") }
    var durationChanged by rememberSaveable { mutableStateOf(false) }
    var durationError by rememberSaveable { mutableStateOf(!validateDuration(duration)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .testTag("track-name"),
            value = name,
            onValueChange = {
                name = it
                nameError = !validateName(name)
                nameChanged = true
            },
            enabled = formEnabled,
            isError = nameError && nameChanged,
            label = { Text("Nombre") },
            singleLine = true,
            supportingText = {
                Text(
                    text = "${name.length} / ${AlbumTrackViewModel.TRACK_NAME_MAX_LENGTH}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "${name.length} de ${AlbumTrackViewModel.TRACK_NAME_MAX_LENGTH} caracteres utilizados" },
                    textAlign = TextAlign.End,
                )
            },
            trailingIcon = {
                if (nameError && nameChanged)
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
            }
        )

        OutlinedTextField(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .testTag("track-duration"),
            value = duration,
            onValueChange = {
                duration = it
                durationError = !validateDuration(duration)
                durationChanged = true
            },
            enabled = formEnabled,
            isError = durationError && durationChanged,
            label = { Text("Duración") },
            singleLine = true,
            supportingText = {
                Text(
                    text = "mm:ss (mm=minutos, ss=segundos)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Formato: minutos : segundos" },
                    textAlign = TextAlign.Start,
                )
            },
            trailingIcon = {
                if (durationError && durationChanged)
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onSave(name, duration)
                },
                enabled = formEnabled && !nameError && !durationError,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("track-submit")
            ) {
                if (formEnabled) {
                    Text(
                        text = "Agregar",
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    CircularProgressIndicator(
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(
                            with(LocalDensity.current) {
                                MaterialTheme.typography.titleMedium.lineHeight.toDp()
                            }
                        )
                    )
                }
            }
        }
    }
}
