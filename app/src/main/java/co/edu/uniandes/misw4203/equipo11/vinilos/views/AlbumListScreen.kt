package co.edu.uniandes.misw4203.equipo11.vinilos.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.repositories.AlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.theme.VinilosTheme
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.AlbumListErrorUiState
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.AlbumListViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.AlbumListViewModel.Companion.KEY_ALBUM_REPOSITORY
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.ErrorUiState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder

@Composable
fun AlbumListScreen(snackbarHostState: SnackbarHostState) {
    val viewModel: AlbumListViewModel = viewModel(
        factory = AlbumListViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(KEY_ALBUM_REPOSITORY, AlbumRepository())
        }
    )
    val albums by viewModel.albums.collectAsStateWithLifecycle(
        emptyList()
    )
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle(
        true
    )
    val error by viewModel.error.collectAsStateWithLifecycle(
        AlbumListErrorUiState(ErrorUiState.NoError)
    )
    val errorState = error.errorState

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.onRefresh() }
    )

    Box(Modifier.pullRefresh(pullRefreshState)) {
        AlbumList(albums)

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    if (errorState is ErrorUiState.Error) {
        val message = stringResource(errorState.resourceId)
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
private fun AlbumItem(album: Album) {
    var coverPreview: Placeholder? = null
    if (LocalInspectionMode.current) {
        coverPreview = placeholder(ColorPainter(Color(album.cover.toColorInt())))
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .testTag("album-list-item"),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        shape = RectangleShape,
        onClick = { /*TODO*/ }
    ) {
        Column {
            GlideImage(
                model = album.cover,
                contentDescription = null,
                loading = coverPreview,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
            Text(
                text = album.name,
                modifier = Modifier
                    .padding(4.dp, 4.dp, 4.dp, 1.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = album.genre,
                modifier = Modifier
                    .padding(4.dp, 1.dp, 4.dp, 4.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun AlbumList(albums: List<Album>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(180.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(albums) {
                item: Album -> AlbumItem(item)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AlbumListScreenPreview() {
    @Suppress("SpellCheckingInspection")
    val albums: List<Album> = listOf(
        Album("Buscando américa", "Salsa", "red"),
        Album("Lo mas lejos a tu lado", "Rock", "green"),
        Album("Pa'lla Voy", "Salsa", "yellow"),
        Album("Recordando el Ayer", "Salsa", "blue"),
        Album("Único", "Salsa", "magenta"),
        Album("Vagabundo", "Salsa", "olive"),
    )

    VinilosTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AlbumList(albums)
        }
    }
}
