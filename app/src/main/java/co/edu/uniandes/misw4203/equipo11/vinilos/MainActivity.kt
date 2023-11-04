package co.edu.uniandes.misw4203.equipo11.vinilos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import co.edu.uniandes.misw4203.equipo11.vinilos.models.VinilosDB
import co.edu.uniandes.misw4203.equipo11.vinilos.views.InitialScreen
import co.edu.uniandes.misw4203.equipo11.vinilos.views.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            this,
            VinilosDB::class.java,
            "vinilos.db"
        ).build()

        VinilosDB.setInstance(db)

        setContent {
            val navController = rememberNavController()

            val snackbarHostState = remember { SnackbarHostState() }

            NavHost(navController = navController, startDestination = "login") {
                composable("login") { LoginScreen(mainNavController = navController) }
                composable("initial") { InitialScreen(mainNavController = navController, snackbarHostState = snackbarHostState) }
            }
        }
    }
}
