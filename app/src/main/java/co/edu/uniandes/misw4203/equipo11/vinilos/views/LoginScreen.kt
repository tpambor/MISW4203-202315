package co.edu.uniandes.misw4203.equipo11.vinilos.views


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavHostController
import co.edu.uniandes.misw4203.equipo11.vinilos.PreferenceDataStore
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.theme.VinilosTheme
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder
import kotlinx.coroutines.runBlocking


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun LoginScreen(mainNavController: NavHostController) {
    val preferenceDataStore = PreferenceDataStore(LocalContext.current)

    val logo = "https://i.imgur.com/9IWVz0q.png"
    var coverPreview: Placeholder? = null
    if (LocalInspectionMode.current) {
        coverPreview = placeholder(ColorPainter(Color(logo.toColorInt())))
    }

    VinilosTheme {
        Scaffold(
            content = { padding ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        GlideImage(
                            model = logo,
                            contentDescription = null,
                            loading = coverPreview,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .size(230.dp)
                                .aspectRatio(1f)
                                .align(Alignment.CenterHorizontally),
                            contentScale = ContentScale.Crop
                        )
                        Text(text = "¿Cómo quieres ingresar?",
                        modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 10.dp))
                        Row {
                            Button(
                                onClick = {
                                    mainNavController.navigate("initial")
                                    runBlocking {
                                        preferenceDataStore.saveUserType("Visitante")
                                    }
                                },
                                modifier = Modifier
                                    .padding(0.dp, 0.dp, 16.dp, 0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Text("Visitante")
                            }
                            Button(
                                onClick = {
                                    mainNavController.navigate("initial")
                                    runBlocking {
                                        preferenceDataStore.saveUserType("Coleccionista")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Text("Coleccionista")
                            }
                        }
                    }
                }
            }
        )
    }
}