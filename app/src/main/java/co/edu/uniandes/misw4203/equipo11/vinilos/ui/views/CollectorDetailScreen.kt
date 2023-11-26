package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.compose.foundation.background
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
import androidx.compose.material3.Badge
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Collector
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorAlbum
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorAlbumStatus
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.CollectorRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.theme.VinilosTheme
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.CollectorViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import java.time.Instant

@Composable
fun CollectorDetailScreen(collectorId: Int, snackbarHostState: SnackbarHostState, navController: NavHostController) {
    val viewModel: CollectorViewModel = viewModel(
        factory = CollectorViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(CollectorViewModel.KEY_COLLECTOR_REPOSITORY, CollectorRepository())
            set(CollectorViewModel.KEY_COLLECTOR_ID, collectorId)
        }
    )

    val collector by viewModel.collector.collectAsStateWithLifecycle(
        null
    )
    val favoritePerformers by viewModel.favoritePerformers.collectAsStateWithLifecycle(
        emptyList()
    )
    val albums by viewModel.albums.collectAsStateWithLifecycle(
        emptyList()
    )

    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle(
        true
    )
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.onRefresh() }
    )

    val error by viewModel.error.collectAsStateWithLifecycle(
        ErrorUiState.NoError
    )

    Box(Modifier.pullRefresh(pullRefreshState)) {
        collector?.let { CollectorDetail(it, favoritePerformers, albums, navController) }

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
            viewModel.onErrorShown()
        }
    }
}

@Composable
private fun CollectorHeader(collector: Collector) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 8.dp)
            .testTag("collector-detail-header"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = collector.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp,
        )
        Text(
            text = collector.email,
            fontSize = 14.sp,
        )
        Text(
            text = collector.telephone,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun FavoritePerformersList(performers: List<Performer>, navController: NavHostController) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 8.dp, 8.dp, 0.dp)
            .testTag("collector-detail-artist-list"),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(performers) { item: Performer ->
            ArtistItem(
                item,
                favButton = {},
                navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
private fun CollectorAlbumItem(album: CollectorAlbum, navController: NavHostController) {
    ElevatedCard(
        onClick = { navController.navigate("albums/${album.album.id}") },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            GlideImage(
                model = album.album.cover,
                contentDescription = "Album",
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.outlineVariant),
                contentScale = ContentScale.Crop,
                transition = CrossFade
            )
            Text(
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                text = album.album.name
            )
            Badge(
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 8.dp)
                    .semantics { this.contentDescription = "Estado" },
                containerColor =
                    if (album.status == CollectorAlbumStatus.Active)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.outlineVariant
            ) {
                Text(
                    text =
                        if (album.status == CollectorAlbumStatus.Active)
                            "Activo"
                        else
                            "Inactivo"
                )
            }
            Text(
                modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                text = "\$ %,d".format(album.price)
            )
            Text(
                modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                text = album.album.description
            )
        }
    }
}

@Composable
private fun AlbumList(albums: List<CollectorAlbum>, navController: NavHostController) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 8.dp, 8.dp, 0.dp)
            .testTag("album-list"),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(albums) { item: CollectorAlbum ->
            CollectorAlbumItem(item, navController)
        }
    }
}

@Composable
private fun CollectorDetail(collector: Collector, favoritePerformers: List<Performer>, albums: List<CollectorAlbum>, navController: NavHostController) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }

    val tabs = listOf(
        "Artistas favoritos",
        "Álbumes"
    )

    Column {
        CollectorHeader(collector)

        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.testTag("collector-detail-tabs")
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index  }
                )
            }
        }

        when (tabIndex) {
            0 -> FavoritePerformersList(favoritePerformers, navController)
            1 -> AlbumList(albums, navController)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CollectorDetailScreenPreview() {
    val collector = Collector(
        id = 1,
        name = "Manolo Bellón",
        telephone = "3502457896",
        email = "manollo@caracol.com.co"
    )

    val performers = listOf(
        Performer(
            id = 1,
            PerformerType.BAND,
            name = "Alcolirycoz",
            "red",
            "Lorem ipsum",
            Instant.now()
        ),
        Performer(
            id = 2,
            PerformerType.MUSICIAN,
            name = "Rubén Blades",
            "blue",
            "Cantante salsa",
            Instant.now()
        )
    )

    VinilosTheme {
        CollectorDetail(collector, performers, emptyList(), rememberNavController())
    }
}
