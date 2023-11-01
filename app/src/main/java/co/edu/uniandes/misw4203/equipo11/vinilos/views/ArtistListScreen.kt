package co.edu.uniandes.misw4203.equipo11.vinilos.views

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
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
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Musician
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.integration.compose.GlideImage
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.theme.VinilosTheme
import java.util.Date

/*
The artist list screen has two tabs, one for the list of artists and one for the list of bands.
Each tab has a list of artists or bands, respectively.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ArtistListScreen() {
    val tabs = listOf("Musicos", "Bandas")
    val selectedTabIndex = remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TabRow(
                selectedTabIndex.intValue,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex.intValue == index,
                        onClick = { selectedTabIndex.intValue = index },
                        text = { Text(title) },
                        modifier = Modifier.testTag(title)
                    )
                }
            }
        }

    ) {
        Crossfade(selectedTabIndex.intValue, label = "") { index ->
            when (index) {
                0 -> ArtistListView()
                1 -> BandListView()
            }
        }
    }
}

/*
Musician Item to be displayed in the list of musicians.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
private fun ArtistItem(musician: Musician){
    var coverPreview: Placeholder? = null
    if (LocalInspectionMode.current){
        coverPreview = placeholder(ColorPainter(Color(musician.image.toColorInt())))
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .testTag("artist-list-item"),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        shape = RectangleShape,
        onClick = { /*TODO*/ }
        ) {
        Column {
            GlideImage(
                model = musician.image,
                contentDescription = null,
                loading = coverPreview,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
            Text(
                text = musician.name,
                modifier = Modifier
                    .padding(4.dp, 4.dp, 4.dp, 1.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

/*
List of musicians to be displayed ArtistListView.
 */
@Composable
private fun ArtistList(musicians: List<Musician>){
    LazyVerticalGrid(
        columns = GridCells.Adaptive(180.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(musicians){
                item: Musician -> ArtistItem(item)
        }
    }
}

@Composable
fun ArtistListView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Lista de Artistas")
    }
}

@Composable
fun BandListView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Lista de Bandas")
    }

}
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ArtistsListScreenPreview(){
    val artists: List<Musician> = listOf(
        Musician(1, "Juanes", "green", "", Date()),
        Musician(2, "Shakira", "green", "", Date()),
        Musician(3, "Maluma", "green", "", Date()),
        Musician(4, "J Balvin", "green", "", Date()),
        Musician(5, "Carlos Vives", "green", "", Date()),
        Musician(6, "Sebastian Yatra", "green", "", Date()),
        Musician(7, "Jesse & Joy", "green", "", Date()),
        Musician(8, "Manuel Turizo", "green", "", Date()),
        Musician(9, "Camilo", "green", "", Date()),
        Musician(0, "Karol G", "green", "", Date())
    )
    VinilosTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ArtistList(artists)
        }
    }
}