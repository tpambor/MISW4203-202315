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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.PerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.BandAddMusicianViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.FormUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BandAddMusicianScreen(snackbarHostState: SnackbarHostState, artistId: Int, navController: NavHostController, activityScope: CoroutineScope) {
    val viewModel: BandAddMusicianViewModel = viewModel(
        factory = BandAddMusicianViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(BandAddMusicianViewModel.KEY_PERFORMER_REPOSITORY, PerformerRepository())
            set(BandAddMusicianViewModel.KEY_PERFORMER_ID, artistId)
        }
    )

    val band by viewModel.band.collectAsStateWithLifecycle(
        null
    )
    val candidates by viewModel.membersCandidates.collectAsStateWithLifecycle(
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
            activityScope.launch {
                snackbarHostState.showSnackbar("Músico agregado a banda")
            }
            navController.navigate("artists/band/$artistId") {
                popUpTo("artists/band/$artistId")

                launchSingleTop = true
            }
        }
    }

    band?.let {
        BandAddMusician(
            band = it,
            candidates = candidates,
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
private fun BandAddMusician(band: Performer, candidates: List<Performer>, state: FormUiState, onSave: (Int) -> Unit) {
    var openConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    var selectedMusicianName by rememberSaveable { mutableStateOf("") }
    var selectedMusicianId by rememberSaveable { mutableIntStateOf(0) }

    if (openConfirmationDialog) {
        ConfirmationDialog(
            onDismissRequest = { openConfirmationDialog = false },
            onConfirmation = {
                onSave(selectedMusicianId)
                openConfirmationDialog = false
             },
            dialogTitle = "Agregar músico",
            dialogText = "¿Quieres agregar $selectedMusicianName a la banda ${band.name}?"
        )
    }

    if (state != FormUiState.Input) {
        ProgressDialog(title = "Guardando...")
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .semantics { contentDescription = "Lista de músicos para agregar a la banda"  },
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(candidates) {
            item: Performer -> ArtistItem(
                performer = item,
                favButton = {},
                onClick = {
                    selectedMusicianName = item.name
                    selectedMusicianId = item.id
                    openConfirmationDialog = true
                },
            )
        }
    }
}
