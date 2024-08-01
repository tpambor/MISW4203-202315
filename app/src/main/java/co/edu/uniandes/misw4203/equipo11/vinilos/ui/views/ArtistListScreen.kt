package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.User
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.UserType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.PerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.UserRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.theme.VinilosTheme
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.PerformerListViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.UserViewModel
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistListScreen(snackbarHostState: SnackbarHostState, navController: NavHostController) {
    val userRepository = UserRepository()
    val viewModel: PerformerListViewModel = viewModel(
        factory = PerformerListViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, PerformerRepository())
            set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
        }
    )
    val musicians by viewModel.musicians.collectAsStateWithLifecycle(
        emptyList()
    )

    val bands by viewModel.bands.collectAsStateWithLifecycle(
        emptyList()
    )

    val favoritePerformers by viewModel.favoritePerformers.collectAsStateWithLifecycle(
        emptySet()
    )

    val updatingFavoritePerformers by viewModel.updatingFavoritePerformers.collectAsStateWithLifecycle(
        emptySet()
    )

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(UserViewModel.KEY_USER_REPOSITORY, userRepository)
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

    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(isRefreshing) {
        if (isRefreshing && !pullRefreshState.isRefreshing) {
            pullRefreshState.startRefresh()
        }
        else if (!isRefreshing && pullRefreshState.isRefreshing) {
            pullRefreshState.endRefresh()
        }
    }

    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            when(tabIndex) {
                0 -> viewModel.onRefreshMusicians()
                1 -> viewModel.onRefreshBands()
            }
        }
    }

    Box(Modifier.nestedScroll(pullRefreshState.nestedScrollConnection)) {
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
                0 -> ArtistsList(
                    musicians,
                    user,
                    favoritePerformers,
                    updatingFavoritePerformers,
                    "musicians",
                    viewModel::addFavoriteMusician,
                    viewModel::removeFavoriteMusician,
                    navController
                )
                1 -> ArtistsList(
                    bands,
                    user,
                    favoritePerformers,
                    updatingFavoritePerformers,
                    "bands",
                    viewModel::addFavoriteBand,
                    viewModel::removeFavoriteBand,
                    navController
                )
            }
        }

        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = pullRefreshState
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
private fun FavoriteButton(
    performerId: Int,
    performerName: String,
    isFavorite: Boolean,
    isUpdating: Boolean,
    addFavoritePerformer: (Int) -> Unit,
    removeFavoritePerformer: (Int) -> Unit
) {
    if(isUpdating) {
        CircularProgressIndicator(modifier = Modifier
            .size(48.dp)
            .padding(2.dp, 2.dp, 3.dp, 2.dp)
        )
    } else {
        IconButton(
            onClick = {
                if (isFavorite)
                    removeFavoritePerformer(performerId)
                else
                    addFavoritePerformer(performerId)
            },
            modifier = Modifier
                .background(
                    color = if (isFavorite) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background,
                    shape = CircleShape,
                )
                .size(48.dp)
                .padding(0.dp, 0.dp, 1.dp, 0.dp)
                .testTag(if (isFavorite) "performer-fav-button-checked" else "performer-fav-button-unchecked")
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = if (isFavorite) stringResource(R.string.artists_delete_favorite, performerName) else stringResource(R.string.artists_add_favorite, performerName),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ArtistItem(
    performer: Performer,
    favButton: @Composable () -> Unit,
    onClick: () -> Unit
) {
    var coverPreview: Placeholder? = null
    if (LocalInspectionMode.current) {
        coverPreview = placeholder(ColorPainter(Color(performer.image.toColorInt())))
    }

    Card(
        modifier = Modifier
            .testTag("performer-list-item"),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        shape = RectangleShape,
        onClick = onClick
    ) {
        Column {
            GlideImage(
                model = performer.image,
                contentDescription = null,
                loading = coverPreview,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.outlineVariant),
                contentScale = ContentScale.Crop,
                transition = CrossFade
            )
            Row (modifier = Modifier.padding(4.dp)){
                Text(
                    text = performer.name,
                    modifier = Modifier
                        .padding(4.dp, 8.dp, 4.dp, 0.dp)
                        .fillMaxWidth()
                        .weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )

                favButton()
            }
        }
    }
}

@SuppressWarnings("kotlin:S107") // Exception: This function has more than 7 parameters as it is a stateless composable which receives the data from the caller
@Composable
private fun ArtistsList(
    performers: List<Performer>,
    user: User?,
    favoritePerformers: Set<Int>,
    updatingFavoritePerformers: Set<Int>,
    tab: String,
    addFavoritePerformer: (Int) -> Unit,
    removeFavoritePerformer: (Int) -> Unit,
    navController: NavHostController
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 8.dp, 8.dp, 0.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (performers.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier.heightIn(50.dp).fillMaxSize()
                ) {
                    val message = when (tab) {
                        "musicians" -> stringResource(R.string.empty_musicians_list)
                        "bands" -> stringResource(R.string.empty_bands_list)
                        else -> ""
                    }

                    Text(text = message)
                }
            }
        }

        items(performers) { item: Performer ->
            val isFavorite = favoritePerformers.contains(item.id)
            val isUpdating = updatingFavoritePerformers.contains(item.id)

            ArtistItem(
                item,
                favButton = {
                    // Favorite button
                    if (user?.type == UserType.Collector) {
                        FavoriteButton(item.id, item.name, isFavorite, isUpdating, addFavoritePerformer, removeFavoritePerformer)
                    }
                },
                onClick = {
                    val prefix = if (item.type == PerformerType.MUSICIAN) "musician" else "band"
                    navController.navigate("artists/$prefix/${item.id}")
                }
            )
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
                    0 ->  ArtistsList(musician, user, emptySet(), emptySet(),"musicians", addFavoritePerformer = {}, removeFavoritePerformer = {}, rememberNavController())
                    1 ->  ArtistsList(bands, user, emptySet(), emptySet(), "bands", addFavoritePerformer = {}, removeFavoritePerformer = {}, rememberNavController())
                }
            }
        }
    }
}
