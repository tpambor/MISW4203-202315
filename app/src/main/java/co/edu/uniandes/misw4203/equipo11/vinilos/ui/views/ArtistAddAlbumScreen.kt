package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.PerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ArtistAddAlbumViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.FormUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ArtistAddAlbumScreen(snackbarHostState: SnackbarHostState, artistId: Int, navController: NavHostController, activityScope: CoroutineScope, type: PerformerType) {
    val viewModel: ArtistAddAlbumViewModel = viewModel(
        factory = ArtistAddAlbumViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(ArtistAddAlbumViewModel.KEY_PERFORMER_REPOSITORY, PerformerRepository())
            set(ArtistAddAlbumViewModel.KEY_PERFORMER_ID, artistId)
            set(ArtistAddAlbumViewModel.KEY_PERFORMER_TYPE, type)
        }
    )

    val performer by viewModel.performer.collectAsStateWithLifecycle(
        null
    )
    val albums by viewModel.albums.collectAsStateWithLifecycle(
        emptyList()
    )

    val state by viewModel.state.collectAsStateWithLifecycle(
        FormUiState.Input
    )
    val error by viewModel.error.collectAsStateWithLifecycle(
        ErrorUiState.NoError
    )

    if (state == FormUiState.Saved) {
        LaunchedEffect(state) {
            val message = if (type == PerformerType.MUSICIAN)
                "Álbum agregado a músico"
            else
                "Álbum agregado a banda"

            activityScope.launch {
                snackbarHostState.showSnackbar(message)
            }
            val prefix = if (type == PerformerType.MUSICIAN) "musician" else "band"
            navController.navigate("artists/$prefix/$artistId") {
                popUpTo("artists/$prefix/$artistId")

                launchSingleTop = true
            }
        }
    }

    performer?.let {
        ArtistAddAlbum(
            performer = it,
            albums = albums,
            type = type,
            state = state,
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

@Composable
private fun ArtistAddAlbum(performer: Performer, albums: List<Album>, type: PerformerType, state: FormUiState, onSave: (Int) -> Unit) {
    var openConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    var selectedAlbumName by rememberSaveable { mutableStateOf("") }
    var selectedAlbumId by rememberSaveable { mutableIntStateOf(0) }

    if (openConfirmationDialog) {
        ConfirmationDialog(
            onDismissRequest = { openConfirmationDialog = false },
            onConfirmation = {
                onSave(selectedAlbumId)
                openConfirmationDialog = false
            },
            dialogTitle = "Agregar álbum",
            dialogText = if (type == PerformerType.MUSICIAN)
                "¿Quieres agregar el álbum $selectedAlbumName al músico ${performer.name}?"
            else
                "¿Quieres agregar el álbum $selectedAlbumName a la banda ${performer.name}?"
        )
    }

    if (state != FormUiState.Input) {
        ProgressDialog(title = "Guardando...")
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(albums) { item ->
            AlbumItem(
                album = item,
                onClick = {
                    selectedAlbumName = item.name
                    selectedAlbumId = item.id
                    openConfirmationDialog = true
                }
            )
        }
    }
}
