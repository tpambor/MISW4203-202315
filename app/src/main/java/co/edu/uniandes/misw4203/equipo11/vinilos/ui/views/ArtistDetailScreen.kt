package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.PerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.theme.VinilosTheme
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.MusicianViewModel
import java.time.Instant

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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ArtistDetailScreenPreview() {
    var performer = Performer(1, PerformerType.MUSICIAN,"Rubén Blades Bellido de Luna","red", "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.", Instant.now())

    VinilosTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                
            }
        }
    }

}
