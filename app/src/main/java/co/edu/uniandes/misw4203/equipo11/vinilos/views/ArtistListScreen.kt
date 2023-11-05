package co.edu.uniandes.misw4203.equipo11.vinilos.views


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.models.User
import co.edu.uniandes.misw4203.equipo11.vinilos.models.UserType
import co.edu.uniandes.misw4203.equipo11.vinilos.repositories.PerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.repositories.UserRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.theme.VinilosTheme
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.ErrorUiState
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.PerformerListViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.UserViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder
import java.time.Instant

@Composable
fun ArtistListScreen(snackbarHostState: SnackbarHostState) {
    val viewModel: PerformerListViewModel = viewModel(
        factory = PerformerListViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, PerformerRepository())
        }
    )
    val musicians by viewModel.musicians.collectAsStateWithLifecycle(
        emptyList()
    )

    val bands by viewModel.bands.collectAsStateWithLifecycle(
        emptyList()
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

    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle(
        true
    )

    val error by viewModel.error.collectAsStateWithLifecycle(
        ErrorUiState.NoError
    )

    val tabs = listOf(
        stringResource(R.string.artists_tab_musicans),
        stringResource(R.string.artists_tab_bands)
    )

    var tabIndex by rememberSaveable { mutableIntStateOf(0) }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
                when(tabIndex) {
                0 -> viewModel.onRefreshMusicians()
                1 -> viewModel.onRefreshBands()
            }
        }
    )

    Box(Modifier.pullRefresh(pullRefreshState)) {
        Column {
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index  },
                        modifier = Modifier.testTag(title)
                    )
                }
            }
            when (tabIndex) {
                0 -> ArtistsList(performers = musicians, user, "musicians")
                1 -> ArtistsList(performers = bands, user, "bands")
            }
        }

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
private fun ArtistItem(performer: Performer, user: User?) {
    var isFavorite by remember { mutableStateOf(false) }

    val isCollector = user?.type == UserType.Collector

    // TODO: Agregar lógica para manejar la acción de agregar/quitar de favoritos

    var coverPreview: Placeholder? = null
    if (LocalInspectionMode.current) {
        coverPreview = placeholder(ColorPainter(Color(performer.image.toColorInt())))
    }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .testTag("performer-list-item"),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        shape = RectangleShape,
        onClick = { /*TODO: Acción para llevar al detalle*/ }
    ) {
        Column {
            GlideImage(
                model = performer.image,
                contentDescription = null,
                loading = coverPreview,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
            Row (modifier = Modifier.padding(4.dp)){
                Text(
                    text = performer.name,
                    modifier = Modifier
                        .padding(4.dp, 8.dp, 0.dp, 0.dp)
                        .fillMaxWidth()
                        .weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )

                // Favorite button
                if(isCollector){
                    IconButton(
                        onClick = {
                            isFavorite = !isFavorite
                            // TODO: Agregar lógica para manejar la acción de agregar/quitar de favoritos
                        },
                        modifier = Modifier
                            .background(
                                color = if (isFavorite) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background,
                                shape = CircleShape,
                            )
                            .size(35.dp)
                            .padding(0.dp, 0.dp, 1.dp, 0.dp)
                            .testTag("performer-fav-button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(R.string.artists_add_favorite),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun ArtistsList(performers: List<Performer>, user: User?, tab: String) {
    val message = when (tab) {
        "musicians" -> stringResource(R.string.empty_musicians_list)
        "bands" -> stringResource(R.string.empty_bands_list)
        else -> ""
    }

    if(performers.isNotEmpty()){
        LazyVerticalGrid(
            columns = GridCells.Adaptive(180.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(performers) {
                    item: Performer -> ArtistItem(performer = item, user = user)
            }
        }
    }else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = message)
        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ArtistListScreenPreview() {
    val user = User(UserType.Collector, 1)
    val musician: List<Performer> = listOf(
        Performer(1, PerformerType.MUSICIAN,"Rubén Blades Bellido de Luna","red", "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.", Instant.now()),
        Performer(2, PerformerType.MUSICIAN, "Juan Luis Guerra","blue", "Es un cantautor, arreglista, músico, productor musical y empresario dominicano.", Instant.now()),
        Performer(3, PerformerType.MUSICIAN, "Freddie Mercury","green", "Fue un cantante y compositor británico de origen parsi que alcanzó fama mundial por ser el vocalista principal y pianista de la banda de rock Queen.", Instant.now())
    )

    val bands: List<Performer> = listOf(
        Performer(1, PerformerType.BAND, "Queen","red", "Es una banda británica de rock formada en 1970 en Londres.", Instant.now()),
        Performer(2, PerformerType.BAND,"Fania All Starts","blue", "Es una agrupación de salsa y música caribeña que a lo largo de su historia ha experimentado diversos géneros musicales como: el rock, jazz, mambo, soul, y más.", Instant.now()),
    )
    val tabs = listOf(
        stringResource(R.string.artists_tab_musicans),
        stringResource(R.string.artists_tab_bands)
    )

    var tabIndex by remember { mutableIntStateOf(0) }

    VinilosTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                TabRow(selectedTabIndex = tabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(text = { Text(title) },
                            selected = tabIndex == index,
                            onClick = { tabIndex = index }
                        )
                    }
                }
                when (tabIndex) {
                    0 ->  ArtistsList(musician, user, "musicians")
                    1 ->  ArtistsList(bands, user, "bands")
                }
            }
        }
    }
}
