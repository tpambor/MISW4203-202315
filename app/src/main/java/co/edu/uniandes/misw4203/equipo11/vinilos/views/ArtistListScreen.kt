package co.edu.uniandes.misw4203.equipo11.vinilos.views

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/*
The artist list screen has two tabs, one for the list of artists and one for the list of bands.
Each tab has a list of artists or bands, respectively.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ArtistListScreen() {
    val tabs = listOf("Artists", "Bands")
    val selectedTabIndex = remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TabRow(selectedTabIndex.intValue) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex.intValue == index,
                        onClick = { selectedTabIndex.intValue = index },
                        text = { Text(title) }
                    )
                }
            }
        }
    ) {
        Crossfade(selectedTabIndex.intValue, label = "") { index ->
            when (index) {
                0 -> ArtistList()
                1 -> BandList()
            }
        }
    }
}

@Composable
fun ArtistList() {
    Text("Artists")
}

@Composable
fun BandList() {
    Text("Bands")
}
