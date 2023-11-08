package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ArtistDetailScreen(snackbarHostState: SnackbarHostState, artistId: Int?){
    Text("Detalle artista id: ${artistId}")
}