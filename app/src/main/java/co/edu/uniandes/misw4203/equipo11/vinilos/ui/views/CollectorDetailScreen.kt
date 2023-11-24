package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Collector
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorAlbum
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.UserType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.CollectorRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.theme.VinilosTheme
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.CollectorViewModel
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

    collector?.let { CollectorDetail(it, favoritePerformers, albums, navController) }
}

@Composable
private fun CollectorHeader(collector: Collector) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 8.dp),
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
            .padding(8.dp, 8.dp, 8.dp, 0.dp),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectorAlbumItem(album: CollectorAlbum) {
    Card(onClick = { /*TODO*/ }) {
        Text(text = album.album.name)
    }
}

@Composable
private fun AlbumList(albums: List<CollectorAlbum>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 8.dp, 8.dp, 0.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(albums) { item: CollectorAlbum ->
            CollectorAlbumItem(item)
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
            containerColor = MaterialTheme.colorScheme.background
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
            1 -> AlbumList(albums)
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
