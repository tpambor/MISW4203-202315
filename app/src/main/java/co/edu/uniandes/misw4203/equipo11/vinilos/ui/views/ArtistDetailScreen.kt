package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
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
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.PerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.BandViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.MusicianViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.PerformerViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder
import java.time.Instant
import java.time.ZoneId
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
    val performer by viewModel.performer.collectAsStateWithLifecycle(
        null
    )
    val albums by viewModel.albums.collectAsStateWithLifecycle(
        emptyList()
    )

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        when (viewModel.performerType){
            PerformerType.MUSICIAN -> performer?.let { MusicianDetail(it, albums) }
            PerformerType.BAND -> {
                val musicians: List<Performer> = listOf(
                    Performer(2, PerformerType.MUSICIAN, "John Lennon", "data:image/jpeg;base64,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", "John Winston Lennon fue un artista, músico, cantautor, actor, activista, compositor, productor, escritor y pacifista británico, conocido por ser el líder y fundador de la banda de rock The Beatles y considerado uno de los artistas más influyentes del siglo XX", Instant.now()),
                    Performer(3, PerformerType.MUSICIAN, "Paul McCartney", "https://media.revistavanityfair.es/photos/60e83d0d9bf55ca1055ab6c4/4:3/w_2888,h_2166,c_limit/185293.jpg","James Paul McCartney es un cantautor, compositor, músico, multiinstrumentista, escritor, activista, pintor y actor británico; que junto a John Lennon, George Harrison y Ringo Starr.", Instant.now()),
                    Performer(4, PerformerType.MUSICIAN, "George Harrison", "https://static.wikia.nocookie.net/doblaje/images/b/b2/Georgeharrison2.jpg/revision/latest/thumbnail/width/360/height/450?cb=20120521203120&path-prefix=es", "George Harrison fue un músico multiinstrumentista, compositor, cantautor, productor musical, productor cinematográfico, actor, filántropo, activista pacifista, ecologista, guitarrista y cantante británico de la banda de rock The Beatles", Instant.now()),
                    Performer(5, PerformerType.MUSICIAN, "Ringo Starr", "https://upload.wikimedia.org/wikipedia/commons/4/4e/Ringo_Starr.png", "Richard Starkey\u200B\u200B, más conocido como Ringo Starr, es un músico, multiinstrumentista, cantante, compositor y actor británico. Fue el baterista de la banda de rock The Beatles.", Instant.now())
                )

                performer?.let { BandDetail(it, albums, musicians, navController) }
            }
        }
    }
}

@Composable
private fun AlbumsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.nav_albums),
            fontSize = 20.sp,
            fontWeight = FontWeight.W500
        )
        Button(
            onClick = { /*TODO*/ },
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

@Composable
private fun MembersHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.members),
            fontSize = 20.sp,
            fontWeight = FontWeight.W500
        )
        Button(
            onClick = { /*TODO*/ },
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

@Composable
private fun MusicianDetail(musician: Performer, albums: List<Album>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(100.dp),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                ArtistDescription(musician)
                AlbumsHeader()
            }
        }
        items(albums) {
                item: Album -> AlbumItem(item)
        }
    }
}

@Composable
private fun BandDetail(band: Performer, albums: List<Album>, members: List<Performer>, navController: NavHostController) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(100.dp),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                ArtistDescription(band)
                MembersHeader()
            }
        }
        items(members) {
                item: Performer -> ArtistItem(
                    performer = item,
                    isCollector = false,
                    isFavorite = false,
                    isUpdating = false,
                    addFavoritePerformer = {},
                    removeFavoritePerformer = {},
                    navController = navController
                )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column{
                AlbumsHeader()
            }
        }
        items(albums) {
                item: Album -> AlbumItem(item)
        }
    }
}

fun birthDateFormatted(performer: Performer): String {
    val birthDate = performer.birthDate.atZone(ZoneId.systemDefault()).toLocalDate()
    val birthDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return birthDate.format(birthDateFormat)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ArtistDescription(performer: Performer){
    var coverPreview: Placeholder? = null
    if (LocalInspectionMode.current) {
        coverPreview = placeholder(ColorPainter(Color(performer.image.toColorInt())))
    }

    Column {
        GlideImage(
            model = performer.image,
            contentDescription = null,
            loading = coverPreview,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .aspectRatio(1.7f),
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 10.dp),
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
                text = stringResource(if (performer.type == PerformerType.MUSICIAN) R.string.musician_birthDate else R.string.band_birthDate),
                fontSize = 14.sp,
                fontWeight = FontWeight.W300,
                color = MaterialTheme.colorScheme.outline,
                letterSpacing = 0.25.sp
            )
            Text(
                text = " " + birthDateFormatted(performer),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.outline,
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
         MusicianDetail(musician, albums)
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
        BandDetail(band, albums, musicians, rememberNavController())
    }
}
