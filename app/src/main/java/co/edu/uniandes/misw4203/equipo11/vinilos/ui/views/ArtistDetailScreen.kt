package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        musician?.let { MusicianDetail(it) }
    }



}

@Composable
fun MusicianDetail(musician: Performer) {
    ArtistDescription(musician)
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
            modifier = Modifier.fillMaxWidth().padding(0.dp, 10.dp),
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
private fun ArtistDetailScreenPreview() {
    val performer = Performer(1, PerformerType.MUSICIAN,"Rubén Blades Bellido de Luna","red", "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.", Instant.now())

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
         MusicianDetail(performer)
    }
}
