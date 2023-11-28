package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Comment
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Track
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.UserType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.AlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.UserRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.AlbumViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.UserViewModel
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AlbumDetailScreen(snackbarHostState: SnackbarHostState, albumId: Int, navController: NavHostController) {
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

    val performers by viewModel.performers.collectAsStateWithLifecycle(
        emptyList()
    )

    val tracks by viewModel.tracks.collectAsStateWithLifecycle(
        emptyList()
    )

    val comments by viewModel.comments.collectAsStateWithLifecycle(
        emptyList()
    )

    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle(
        true
    )

    val error by viewModel.error.collectAsStateWithLifecycle(
        ErrorUiState.NoError
    )

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(UserViewModel.KEY_USER_REPOSITORY, UserRepository())
        }
    )
    val user by userViewModel.user.collectAsStateWithLifecycle(
        null
    )

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.onRefresh() }
    )

    val isCollector = user?.type == UserType.Collector

    Box(Modifier.pullRefresh(pullRefreshState)) {
        album?.let { AlbumDetail(it, performers, tracks, comments, isCollector, navController) }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),

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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun AlbumCover(album: Album) {
    var coverPreview: Placeholder? = null
    if (LocalInspectionMode.current) {
        coverPreview = placeholder(ColorPainter(Color(album.cover.toColorInt())))
    }

    GlideImage(
        model = album.cover,
        contentDescription = null,
        loading = coverPreview,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.outlineVariant)
            .testTag("album-detail-cover"),
        transition = CrossFade,
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun AlbumDetail(
    album: Album,
    performers: List<Performer>,
    tracks: List<Track>,
    comments: List<Comment>,
    isCollector: Boolean,
    navController: NavHostController
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .testTag("album-detail-list"),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Album description section
        item {
            AlbumCover(album)
        }

        item(span = { GridItemSpan(maxCurrentLineSpan) }) {
            AlbumInfo(album)
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            AlbumDescription(album)
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = stringResource(R.string.nav_artists),
                fontSize = 20.sp,
                fontWeight = FontWeight.W500,
                modifier = Modifier.semantics {
                    contentDescription = "Lista de artistas asociados al álbum "
                }
            )
        }

        items(performers) { performer ->
            ArtistItem(
                performer = performer,
                favButton = {},
                navController = navController
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            AlbumsHeader(
                stringResource(R.string.nav_tracks),
                isCollector,
                "Tracks",
                testTag = "add-track",
                onClick = { }
            )
        }

        items(tracks, span = { GridItemSpan(maxLineSpan) }) { track ->
            TrackItem(track)
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            AlbumsHeader(
                stringResource(R.string.nav_comments),
                isCollector,
                "Comentarios",
                testTag = "add-comment",
                onClick = { navController.navigate("albums/${album.id}/comment") }
            )
        }

        items(comments, span = { GridItemSpan(maxLineSpan) }) { comment ->
            CommentItem(comment)
        }
    }
}


@Composable
private fun AlbumsHeader(title: String, isCollector: Boolean, nameComponent: String, testTag: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.W500
        )
        if(isCollector){
            Button(
                onClick = onClick,
                modifier = Modifier
                    .height(40.dp)
                    .semantics { contentDescription = "Agregar $nameComponent" }
                    .testTag(testTag),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(text = "+ Agregar")
            }
        }

    }
}

@Composable
private fun TrackItem(track: Track) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = track.name,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            modifier = Modifier
                .width(100.dp)
                .align(Alignment.CenterVertically),
            text = track.duration,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Right
        )
    }
}

@Composable
private fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .semantics { contentDescription = "Rating  ${comment.rating}" }
            .testTag("album-detail-comment"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = comment.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${comment.rating}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "★"  ,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics {
                    contentDescription = "Estrella ${comment.rating} "
                }
            )
        }
    }
}

@Composable
private fun AlbumInfo(album: Album){
    Column(
        modifier = Modifier
            .padding(start = 8.dp)
            .testTag("album-detail-info")
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
            text = releaseDateFormatted(album),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.outline,
            letterSpacing = 0.25.sp, modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun AlbumDescription(album: Album) {
    Column(
        modifier = Modifier
            .testTag("album-detail-description")
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