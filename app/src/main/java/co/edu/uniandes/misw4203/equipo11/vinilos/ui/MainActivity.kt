package co.edu.uniandes.misw4203.equipo11.vinilos.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.VinilosDB
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.PreferenceDataStore
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.theme.VinilosTheme
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.views.CreateAlbumButton
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.views.NavBar
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.views.NavContent
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.views.TopNavBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PreferenceDataStore.create(applicationContext)
        val db = Room.databaseBuilder(
            this,
            VinilosDB::class.java,
            "vinilos.db"
        ).fallbackToDestructiveMigration().build()
        VinilosDB.setInstance(db)

        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val navController = rememberNavController()
            val currentBackStackEntry by navController.currentBackStackEntryAsState()

            VinilosTheme {
                Scaffold(
                    topBar = {
                        TopNavBar(navController, currentBackStackEntry)
                    },
                    content = { padding ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            NavContent(navController, snackbarHostState)
                        }
                    },
                    bottomBar = {
                        NavBar(navController, currentBackStackEntry)
                    },
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState
                        )
                    },
                    floatingActionButtonPosition = FabPosition.End,
                    floatingActionButton = {
                        if (currentBackStackEntry?.destination?.route == "albums") {
                            // Agrega el FloatingActionButton solo en la pantalla /albums
                            CreateAlbumButton(navController)
                        }
                    }
                )
            }
        }
    }
}
