package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.AlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.PerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.AlbumListViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.MusicianViewModel

@Composable
fun ArtistDetailScreen(snackbarHostState: SnackbarHostState, artistId: Int) {
    val viewModel: MusicianViewModel = viewModel(
        factory = MusicianViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(MusicianViewModel.KEY_PERFORMER_REPOSITORY, PerformerRepository())
            set(MusicianViewModel.KEY_PERFORMER_ID, artistId)
        }
    )

    val musician by viewModel.musician.collectAsStateWithLifecycle(
        null
    )

    musician?.let { MusicianDetail(it) }
}

@Composable
fun MusicianDetail(musician: Performer) {
    Text("Detalle artista: ${musician.name}")
}
