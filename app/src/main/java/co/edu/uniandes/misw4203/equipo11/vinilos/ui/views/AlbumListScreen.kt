package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.compose.foundation.layout.Arrangement
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
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.AlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.theme.VinilosTheme
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.AlbumListViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.AlbumListViewModel.Companion.KEY_ALBUM_REPOSITORY
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder
import java.time.Instant
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun AlbumListScreen(snackbarHostState: SnackbarHostState, navController: NavHostController) {
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
        ErrorUiState.NoError
    )

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.onRefresh() }
    )

    Box(
        Modifier
            .pullRefresh(pullRefreshState)
            .padding(16.dp)) {
        AlbumList(albums,navController)

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    if (error is ErrorUiState.Error) {
        val message = stringResource((error as ErrorUiState.Error).resourceId)
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun AlbumItem(album: Album, navController: NavHostController) {
    var coverPreview: Placeholder? = null
    if (LocalInspectionMode.current) {
        coverPreview = placeholder(ColorPainter(Color(album.cover.toColorInt())))
    }

    Card(
        modifier = Modifier.testTag("album-list-item"),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        shape = RectangleShape,
        onClick = { navController.navigate("albums/${album.id}") }
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
fun AlbumList(albums: List<Album>, navController: NavHostController) {
    if(albums.isNotEmpty()){
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp, 8.dp, 8.dp, 0.dp),

            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(albums) {
                    item: Album -> AlbumItem(item, navController)
            }
        }
    }else{
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = stringResource(R.string.empty_albums_list))
        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AlbumListScreenPreview() {
    @Suppress("SpellCheckingInspection")
    val albums: List<Album> = listOf(
        Album(1, "Buscando américa","red", Instant.now(), "", "Salsa", "" ),
        Album(2,"Lo mas lejos a tu lado", "green", Instant.now(), "", "Rock", ""),
        Album(3, "Pa'lla Voy", "yellow", Instant.now(), "", "Salsa", ""  ),
        Album(4, "Recordando el Ayer","blue", Instant.now(), "", "Salsa", "blue" ),
        Album(5, "Único", "magenta", Instant.now(), "", "Salsa", ""),
        Album(6, "Vagabundo", "olive", Instant.now(), "", "Salsa", ""),
    )

    VinilosTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(Modifier.padding(16.dp)) {
                AlbumList(albums, rememberNavController())
            }

        }
    }
}
