
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Track
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.AlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.AlbumViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AlbumDetailScreen(snackbarHostState: SnackbarHostState, albumId: Int) {
    val viewModel: AlbumViewModel = viewModel(
        factory = AlbumViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(AlbumViewModel.KEY_ALBUM_REPOSITORY, AlbumRepository())
            set(AlbumViewModel.KEY_ALBUM_ID, albumId)
        }
    )

    val album by viewModel.album.collectAsStateWithLifecycle(
        null
    )
    val performances by viewModel.albumsperformers.collectAsStateWithLifecycle(
        emptyList()
    )

    val tracks by viewModel.albumstracks.collectAsStateWithLifecycle(
        emptyList()
    )

    val comments by viewModel.albumscomments.collectAsStateWithLifecycle(
        emptyList()
    )

    val performersList: List<Performer> = listOf(
        Performer(1, PerformerType.MUSICIAN, "Fulanito", "Red", "description", Instant.now()),
        Performer(1, PerformerType.MUSICIAN, "Fulanito", "Red", "description", Instant.now()),
        Performer(1, PerformerType.MUSICIAN, "Fulanito", "Red", "description", Instant.now())

    )



    album?.let { AlbumDetail(it, performances, tracks) }

    // Muestra la lista de pistas
    // TracksList(tracks = album.tracks)

    // Muestra la lista de comentarios
    // CommentsList(comments = album.comments)
}
@Composable
fun PerformersList(performers: List<Performer> ){
    if(performers.isNotEmpty()){
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(performers) {
                    item: Performer -> PerformerItem(item)
            }
        }
    }else{
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("hi")
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
private fun PerformerItem(performer: Performer) {

    Card(
        modifier = Modifier.testTag("album-list-item"),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        shape = RectangleShape,
        onClick = { }
    ) {
        Column {
            GlideImage(
                model = performer.image,
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
            Text(
                text = performer.name,
                modifier = Modifier
                    .padding(4.dp, 4.dp, 4.dp, 1.dp)
                    .fillMaxWidth(),
                style = typography.titleMedium
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
private fun TrackItem(track: Track) {

    Card(
        modifier = Modifier.testTag("album-list-item"),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        shape = RectangleShape,
        onClick = { }
    ) {
        Column {
            Text(
                text = track.name,
                modifier = Modifier
                    .padding(4.dp, 4.dp, 4.dp, 1.dp)
                    .fillMaxWidth(),
                style = typography.titleMedium
            )
            Text(
                text = track.duration,
                modifier = Modifier
                    .padding(4.dp, 4.dp, 4.dp, 1.dp)
                    .fillMaxWidth(),
                style = typography.titleMedium
            )

        }
    }
}
/*
@Composable
fun PerformerImage(imageUrl: String) {
    Image(
        painter = painterResource(id = R.drawable.ic_launcher_foreground),
        contentDescription = null, // Cambia según tus necesidades
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
    )
}

@Composable
fun TracksList(tracks: List<Track>) {
    // Composable para mostrar la lista de pistas con nombre y duración
    // ...
}

@Composable
fun CommentsList(comments: List<Comment>) {
    // Composable para mostrar la lista de comentarios con descripción y calificación
    // ...
}
*/
@Composable
private fun AlbumDetail(album: Album, performer: List<Performer>, tracks: List<Track>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxCurrentLineSpan) }) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                AlbumDescription(album)
            }
        }
        items(performer) {
                item: Performer -> PerformerItem(item)
        }
        items(tracks) {
                item: Track -> TrackItem(item)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AlbumDescription(album: Album){
    var coverPreview: Placeholder? = null
    if (LocalInspectionMode.current) {
        coverPreview = placeholder(ColorPainter(Color(album.cover.toColorInt())))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // First Column
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            GlideImage(
                model = album.cover,
                contentDescription = null,
                loading = coverPreview,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.album_gender),
                fontSize = 14.sp,
                fontWeight = FontWeight.W300,
                color = MaterialTheme.colorScheme.outline,
                letterSpacing = 0.25.sp
            )
            Text(
                text = album.genre,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline,
                letterSpacing = 0.25.sp, modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = stringResource(R.string.album_discografia),
                fontSize = 14.sp,
                fontWeight = FontWeight.W300,
                color = MaterialTheme.colorScheme.outline,
                letterSpacing = 0.25.sp, modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = album.recordLabel,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline,
                letterSpacing = 0.25.sp, modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = stringResource(R.string.album_fechaPublicacion),
                fontSize = 14.sp,
                fontWeight = FontWeight.W300,
                color = MaterialTheme.colorScheme.outline,
                letterSpacing = 0.25.sp, modifier = Modifier.padding(top = 8.dp)

            )
            Text(
                text = " " + releaseDateFormatted(album),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline,
                letterSpacing = 0.25.sp, modifier = Modifier.padding(top = 8.dp)
            )

        }
    }

    Column {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 10.dp),
            text = album.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.W500,
            textAlign = TextAlign.Left,
            lineHeight = 24.sp
        )


        Text(
            text = album.description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.4.sp,
            textAlign = TextAlign.Justify
        )

    }
}

fun releaseDateFormatted(album: Album): String {
    val releaseDate = album.releaseDate.atZone(ZoneId.systemDefault()).toLocalDate()
    val releaseDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return releaseDate.format(releaseDateFormat)
}