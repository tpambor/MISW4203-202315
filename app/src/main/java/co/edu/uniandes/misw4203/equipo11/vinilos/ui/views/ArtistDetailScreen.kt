package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.PerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.MusicianViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ArtistDetailScreen(snackbarHostState: SnackbarHostState, artistId: Int) {
    val viewModel: MusicianViewModel = viewModel(
        factory = MusicianViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(MusicianViewModel.KEY_PERFORMER_REPOSITORY, PerformerRepository())
            set(MusicianViewModel.KEY_PERFORMER_ID, artistId)
        }
    )

    val musician by viewModel.musician.collectAsStateWithLifecycle(
        null
    )

    val albums: List<Album> = listOf(
        Album(1, "Buscando américa","https://upload.wikimedia.org/wikipedia/en/b/b9/Buscando_Am%C3%A9rica.jpg", Instant.now(), "", "Salsa", ""),
        Album(2,"Lo mas lejos a tu lado", "data:image/jpeg;base64,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", Instant.now(), "", "Rock", ""),
        Album(3, "Pa'lla Voy", "https://www.marcanthonyonline.com/wp-content/uploads/2021/08/playlist-profile-image.jpg", Instant.now(), "", "Salsa", "" ),
        Album(4, "Recordando el Ayer","https://fania.com/wp-content/uploads/2021/03/RecordandoElAyer.jpg", Instant.now(), "", "Salsa", "blue"),
        Album(6, "Vagabundo", "https://i.scdn.co/image/ab67616d0000b2732d6016751b8ea5e66e83cd04", Instant.now(), "", "Salsa", ""),
    )

    musician?.let { MusicianDetail(it, albums) }

}


@Composable
private fun MusicianDetail(musician: Performer, albums: List<Album>) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        ArtistDescription(musician)
        Row(
            modifier = Modifier
                .padding(0.dp, 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.nav_albums),
                fontSize = 20.sp,
                fontWeight = FontWeight.W500)
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier.height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(text = "+ Agregar")
            }
        }
        AlbumList(albums, 3)
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
            textAlign = TextAlign.Justify
        )

    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MusicianDetailScreenPreview() {
    val albums: List<Album> = listOf(
        Album(1, "Buscando américa","red", Instant.now(), "", "Salsa", ""),
        Album(2,"Lo mas lejos a tu lado", "green", Instant.now(), "", "Rock", ""),
        Album(3, "Pa'lla Voy", "yellow", Instant.now(), "", "Salsa", "" ),
        Album(4, "Recordando el Ayer","blue", Instant.now(), "", "Salsa", "blue"),
        Album(5, "Único", "magenta", Instant.now(), "", "Salsa", ""),
        Album(6, "Vagabundo", "olive", Instant.now(), "", "Salsa", ""),
    )
    val performer = Performer(1, PerformerType.MUSICIAN,"Rubén Blades Bellido de Luna","red", "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.", Instant.now())

    MusicianDetail(performer, albums)
}