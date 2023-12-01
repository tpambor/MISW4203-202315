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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.UserType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.PerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.UserRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.BandViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.MusicianViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.PerformerViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.UserViewModel
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun MusicianDetailScreen(snackbarHostState: SnackbarHostState, artistId: Int, navController: NavHostController) {
    val viewModel: MusicianViewModel = viewModel(
        factory = MusicianViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(PerformerViewModel.KEY_PERFORMER_REPOSITORY, PerformerRepository())
            set(PerformerViewModel.KEY_PERFORMER_ID, artistId)
        }
    )
    
    PerformerDetailScreen(viewModel, snackbarHostState, navController)
}

@Composable
fun BandDetailScreen(snackbarHostState: SnackbarHostState, artistId: Int, navController: NavHostController) {
    val viewModel: BandViewModel = viewModel(
        factory = BandViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(PerformerViewModel.KEY_PERFORMER_REPOSITORY, PerformerRepository())
            set(PerformerViewModel.KEY_PERFORMER_ID, artistId)
        }
    )

    PerformerDetailScreen(viewModel, snackbarHostState, navController)
}

@Composable
private fun PerformerDetailScreen(viewModel: PerformerViewModel, snackbarHostState: SnackbarHostState, navController: NavHostController) {
    val userRepository = UserRepository()

    val performer by viewModel.performer.collectAsStateWithLifecycle(
        null
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

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(UserViewModel.KEY_USER_REPOSITORY, userRepository)
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

        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            when (viewModel.performerType){
                PerformerType.MUSICIAN -> performer?.let { MusicianDetail(it, albums, navController, isCollector) }
                PerformerType.BAND -> {
                    val musicians by (viewModel as BandViewModel).members.collectAsStateWithLifecycle(
                        emptyList()
                    )

                    performer?.let { BandDetail(it, albums, musicians, navController, isCollector) }
                }
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
            viewModel.onErrorShown()
        }
    }
}

@Composable
private fun AlbumsHeader(isCollector: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("albums-header"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.nav_albums),
            fontSize = 20.sp,
            fontWeight = FontWeight.W500
        )
        if(isCollector){
            Button(
                onClick = onClick,
                modifier = Modifier
                    .height(40.dp)
                    .testTag("add-album"),
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
private fun MembersHeader(isCollector: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("members-header"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.members),
            fontSize = 20.sp,
            fontWeight = FontWeight.W500,
            modifier = Modifier.semantics { this.contentDescription = "Integrantes de la banda" }
        )
        if (isCollector){
            Button(
                onClick = onClick,
                modifier = Modifier
                    .height(40.dp)
                    .testTag("add-member")
                    .semantics {
                        this.contentDescription = "Agregar músico a la banda"
                    },
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
private fun MusicianDetail(musician: Performer, albums: List<Album>, navController: NavHostController, isCollector: Boolean) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = Modifier.fillMaxSize().testTag("artist-detail-list"),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            ArtistDescription(musician)
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            AlbumsHeader(
                isCollector,
                onClick = { navController.navigate("artists/musician/${musician.id}/addAlbum") }
            )
        }

        if (albums.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = stringResource(R.string.empty_albums_list))
                }
            }
        }

        items(albums) {item ->
            AlbumItem(
                album = item,
                onClick = { navController.navigate("albums/${item.id}") }
            )
        }
    }
}

@Composable
private fun BandDetail(band: Performer, albums: List<Album>, members: List<Performer>, navController: NavHostController, isCollector: Boolean) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = Modifier.fillMaxSize().testTag("artist-detail-list"),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                ArtistDescription(band)
                MembersHeader(
                    isCollector,
                    onClick = { navController.navigate("artists/band/${band.id}/addMusician") }
                )
            }
        }
        items(members) {
                item: Performer -> ArtistItem(
                    performer = item,
                    favButton = {},
                    onClick = {
                        val prefix = if (item.type == PerformerType.MUSICIAN) "musician" else "band"
                        navController.navigate("artists/$prefix/${item.id}")
                    }
                )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            AlbumsHeader(
                isCollector,
                onClick = { navController.navigate("artists/band/${band.id}/addAlbum") }
            )
        }

        if (albums.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = stringResource(R.string.empty_albums_list))
                }
            }
        }

        items(albums) { item ->
            AlbumItem(
                album = item,
                onClick = { navController.navigate("albums/${item.id}") }
            )
        }
    }
}

private fun birthDateFormatted(performer: Performer): String {
    val birthDate = performer.birthDate.atOffset(ZoneOffset.UTC).toLocalDate()
    val birthDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return birthDate.format(birthDateFormat)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ArtistDescription(performer: Performer){
    var coverPreview: Placeholder? = null
    if (LocalInspectionMode.current) {
        coverPreview = placeholder(ColorPainter(Color(performer.image.toColorInt())))
    }

    Column(
        modifier = Modifier.testTag("artist-description")
    ) {
        GlideImage(
            model = performer.image,
            contentDescription = "Foto de ${performer.name}",
            loading = coverPreview,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .aspectRatio(1.7f)
                .background(MaterialTheme.colorScheme.outlineVariant),
            contentScale = ContentScale.Crop,
            transition = CrossFade
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 10.dp)
                .semantics {
                           this.contentDescription = "Detalle de ${performer.name}"
                },
            text = performer.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.W500,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        Row (
            modifier = Modifier.padding(0.dp, 12.dp)
        ) {
            Text(
                modifier = Modifier.testTag("birthdate-text"),
                text = stringResource(if (performer.type == PerformerType.MUSICIAN) R.string.musician_birthDate else R.string.band_birthDate),
                fontSize = 14.sp,
                fontWeight = FontWeight.W300,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.25.sp
            )
            Text(
                text = " " + birthDateFormatted(performer),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.25.sp
            )
        }

        Text(
            text = performer.description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.4.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MusicDetailScreenPreview() {
    val musician = Performer(1, PerformerType.MUSICIAN,"Rubén Blades Bellido de Luna","red", "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.", Instant.now())

    val albums: List<Album> = listOf(
        Album(1, "Buscando américa","red", Instant.now(), "", "Salsa", ""),
        Album(3, "Pa'lla Voy", "green", Instant.now(), "", "Salsa", "" ),
        Album(4, "Recordando el Ayer","blue", Instant.now(), "", "Salsa", "blue"),
        Album(6, "Vagabundo", "yellow", Instant.now(), "", "Salsa", ""),
    )

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
         MusicianDetail(musician, albums, rememberNavController(), false)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BandDetailScreenPreview() {
    val band = Performer(1, PerformerType.BAND,"The Beatles","red", "The Beatles, más conocido en el mundo hispano como los Beatles, fue un grupo de rock británico formado en Liverpool durante los años 1960.", Instant.now())
    val musicians: List<Performer> = listOf(
        Performer(2, PerformerType.MUSICIAN, "John Lennon", "blue", "John Winston Lennon fue un artista, músico, cantautor, actor, activista, compositor, productor, escritor y pacifista británico, conocido por ser el líder y fundador de la banda de rock The Beatles y considerado uno de los artistas más influyentes del siglo XX", Instant.now()),
        Performer(3, PerformerType.MUSICIAN, "Paul McCartney", "purple","James Paul McCartney es un cantautor, compositor, músico, multiinstrumentista, escritor, activista, pintor y actor británico; que junto a John Lennon, George Harrison y Ringo Starr.", Instant.now()),
        Performer(4, PerformerType.MUSICIAN, "George Harrison", "yellow", "George Harrison fue un músico multiinstrumentista, compositor, cantautor, productor musical, productor cinematográfico, actor, filántropo, activista pacifista, ecologista, guitarrista y cantante británico de la banda de rock The Beatles", Instant.now()),
        Performer(5, PerformerType.MUSICIAN, "Ringo Starr", "green", "Richard Starkey\u200B\u200B, más conocido como Ringo Starr, es un músico, multiinstrumentista, cantante, compositor y actor británico. Fue el baterista de la banda de rock The Beatles.", Instant.now())
    )
    val albums: List<Album> = listOf(
        Album(1, "Buscando américa","red", Instant.now(), "", "Salsa", ""),
        Album(3, "Pa'lla Voy", "green", Instant.now(), "", "Salsa", "" ),
        Album(4, "Recordando el Ayer","blue", Instant.now(), "", "Salsa", "blue"),
        Album(6, "Vagabundo", "yellow", Instant.now(), "", "Salsa", ""),
    )

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        BandDetail(band, albums, musicians, rememberNavController(), false)
    }
}
