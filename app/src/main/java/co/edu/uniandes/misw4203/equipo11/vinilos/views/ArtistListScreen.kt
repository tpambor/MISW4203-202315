package co.edu.uniandes.misw4203.equipo11.vinilos.views

import android.util.Log
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Band
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Musician
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.repositories.AlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.repositories.PerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.theme.VinilosTheme
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.AlbumListViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.PerformerListViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder
import java.util.Date

@Composable
fun ArtistListScreen() {
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

    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Músicos", "Bandas")
    Box() {
        Column {
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index
                            when (index) {
                                0 -> Log.d("Tab", "Tab Músicos")
                                1 -> viewModel.getBands()
                            }
                        }
                    )
                }
            }
            when (tabIndex) {
                0 ->  ArtistsList(musicians)
                1 ->  ArtistsList(bands)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
private fun ArtistItem(performer: Performer) {
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
        onClick = { /*TODO*/ }
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
            Text(
                text = performer.name,
                modifier = Modifier
                    .padding(4.dp, 4.dp, 4.dp, 1.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun ArtistsList(performers: List<Performer>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(180.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(performers) {
                item: Performer -> ArtistItem(item)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ArtistListScreenPreview() {
    @Suppress("SpellCheckingInspection")
    val musician: List<Musician> = listOf(
        Musician(1, "Rubén Blades Bellido de Luna","red", "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.", Date()),
        Musician(2, "Juan Luis Guerra","blue", "Es un cantautor, arreglista, músico, productor musical y empresario dominicano.", Date()),
        Musician(3, "Freddie Mercury","green", "Fue un cantante y compositor británico de origen parsi que alcanzó fama mundial por ser el vocalista principal y pianista de la banda de rock Queen.", Date())
    )

    val bands: List<Band> = listOf(
        Band(1, "Queen","red", "Es una banda británica de rock formada en 1970 en Londres.", Date()),
        Band(2, "Fania All Starts","blue", "Es una agrupación de salsa y música caribeña que a lo largo de su historia ha experimentado diversos géneros musicales como: el rock, jazz, mambo, soul, y más.", Date()),
    )
    val tabs = listOf("Musicos", "Bandas")
    var tabIndex by remember { mutableStateOf(0) }

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
                    0 ->  ArtistsList(musician)
                    1 ->  ArtistsList(bands)
                }
            }
        }
    }
}