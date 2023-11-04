package co.edu.uniandes.misw4203.equipo11.vinilos.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.theme.VinilosTheme

@Composable
fun InitialScreen(mainNavController: NavHostController, snackbarHostState: SnackbarHostState) {

    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    VinilosTheme {
        Scaffold(
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
                NavBar(navController, mainNavController, currentBackStackEntry)
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState
                )
            }
        )
    }
}