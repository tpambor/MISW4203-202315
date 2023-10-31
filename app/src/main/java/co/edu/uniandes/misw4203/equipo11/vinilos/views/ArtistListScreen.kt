package co.edu.uniandes.misw4203.equipo11.vinilos.views

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

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
                0 -> ArtistList()
                1 -> BandList()
            }
        }
    }
}

@Composable
fun ArtistList() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("List of Artists")
    }
}

@Composable
fun BandList() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("List of Bands")
    }

}
