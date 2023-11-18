import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
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
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Comment
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



    album?.let { AlbumDetail(it, performances, tracks, comments) }
}


@Composable
private fun AlbumDetail(album: Album, performers: List<Performer>, tracks: List<Track>, comments: List<Comment> ) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Album description section
        item(span = { GridItemSpan(maxCurrentLineSpan) }) {
            Column(
                modifier = Modifier.testTag("album-description")
            ) {
                AlbumDescription(album)
            }
        }

        item(span = { GridItemSpan(maxCurrentLineSpan) }) {
            Column(
                modifier = Modifier.padding(8.dp)
                    .testTag("performer-list")
            ) {
                Text(
                    text = stringResource(R.string.nav_artists),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W500,
                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                )
            }
        }

        items(performers) { performer ->
            PerformerItem(performer)
        }
        item(span = { GridItemSpan(maxCurrentLineSpan) }) {

                Text(
                    text = "",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W500,
                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                )

        }
        item(span = {  GridItemSpan(maxCurrentLineSpan) }) {
            AlbumsHeader(stringResource(R.string.nav_tracks).toString())
        }
        items(tracks, span = { GridItemSpan(maxLineSpan) }) { track ->
            TrackItem(track)
        }

        item(span = { GridItemSpan(maxCurrentLineSpan) }) {

                Text(
                    text = "",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W500,
                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                )

        }

        item(span = { GridItemSpan(maxCurrentLineSpan) }) {
            AlbumsHeader(stringResource(R.string.nav_comments).toString())

        }

        items(comments, span = { GridItemSpan(maxLineSpan) }) { comment ->
            CommentItem(comment)
        }
    }
}


@Composable
private fun AlbumsHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("albums-header"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.W500
        )
        Button(
            onClick = { },
            modifier = Modifier
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Text(text = "+ Agregar")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
private fun PerformerItem(performer: Performer) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            ,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        shape = RectangleShape,
        onClick = { /* Handle click event */ }
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
        ) {

            GlideImage(
                model = performer.image,
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .width(100.dp)
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
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = track.name, style = typography.titleMedium, modifier = Modifier.weight(1f))
        Text(text = track.duration, style = typography.titleMedium, modifier = Modifier.width(100.dp), textAlign = TextAlign.Right)
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
private fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = comment.description, style = typography.titleMedium, modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = comment.rating.toString(), style = typography.titleMedium)
            Icon(
                painter = painterResource(id = R.drawable.ic_star),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun AlbumDescription(album: Album){
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
                .testTag("data-albumDetail")
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

    Column(
        modifier = Modifier
            .testTag("description-albumDetail")
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 10.dp),
            text = album.name,
            fontSize = 16.sp,
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

private fun releaseDateFormatted(album: Album): String {
    val releaseDate = album.releaseDate.atZone(ZoneId.systemDefault()).toLocalDate()
    val releaseDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return releaseDate.format(releaseDateFormat)
}