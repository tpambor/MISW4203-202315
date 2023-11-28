package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
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
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.UserRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.AlbumCommentViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.FormUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AlbumCommentScreen(snackbarHostState: SnackbarHostState, albumId: Int, navController: NavHostController, activityScope: CoroutineScope) {
    val viewModel: AlbumCommentViewModel = viewModel(
        factory = AlbumCommentViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(AlbumCommentViewModel.KEY_ALBUM_REPOSITORY, AlbumRepository())
            set(AlbumCommentViewModel.KEY_ALBUM_ID, albumId)
            set(AlbumCommentViewModel.KEY_USER_REPOSITORY, UserRepository())
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
                snackbarHostState.showSnackbar("Comentario agregado exitosamente")
            }
            navController.navigate("albums/$albumId")
        }
    }

    val focusManager = LocalFocusManager.current

    focusManager.clearFocus()

    AlbumComment(
        state = state,
        focusManager = focusManager,
        validateComment = viewModel::validateComment,
        onSave = viewModel::onSave
    )

    if (error is ErrorUiState.Error) {
        val message = stringResource((error as ErrorUiState.Error).resourceId)
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
            viewModel.onErrorShown()
        }
    }
}

@Composable
private fun Rating(rating: Int, formEnabled: Boolean, onClick: (Int) -> Unit) {
    Row {
        for (i in 1..5) {
            IconButton(
                enabled = formEnabled,
                onClick = { onClick(i) }
            ) {
                Icon(
                    if (i <= rating) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "$i de 5"
                )
            }
        }
    }
}

@Composable
private fun AlbumComment(
    state: FormUiState,
    focusManager: FocusManager,
    validateComment: (String) -> Boolean,
    onSave: (Int, String) -> Unit,
) {
    val formEnabled = state == FormUiState.Input

    var rating by rememberSaveable { mutableIntStateOf(5) }
    var comment by rememberSaveable { mutableStateOf("") }
    var commentChanged by rememberSaveable { mutableStateOf(false) }
    var commentError by rememberSaveable { mutableStateOf(validateComment(comment)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Rating",
                style = MaterialTheme.typography.titleLarge
            )

            Rating(
                rating = rating,
                formEnabled = formEnabled,
                onClick = {
                    focusManager.clearFocus()
                    rating = it
                }
            )
        }

        OutlinedTextField(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            value = comment,
            onValueChange = {
                comment = it
                commentError = validateComment(comment)
                commentChanged = true
            },
            enabled = formEnabled,
            isError = commentError && commentChanged,
            label = { Text("Comentario") },
            singleLine = true,
            supportingText = {
                Text(
                    text = "${comment.length} / ${AlbumCommentViewModel.COMMENT_MAX_LENGTH}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "${comment.length} de ${AlbumCommentViewModel.COMMENT_MAX_LENGTH} caracteres utilizados" },
                    textAlign = TextAlign.End,
                )
            },
            trailingIcon = {
                if (commentError && commentChanged)
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
                    onSave(rating, comment)
                },
                enabled = formEnabled && !commentError,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
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
