package co.edu.uniandes.misw4203.equipo11.vinilos.views

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import co.edu.uniandes.misw4203.equipo11.vinilos.R

sealed class NavBarItem(val route: String, @StringRes val stringId: Int, @DrawableRes val iconId: Int) {
    object Albums : NavBarItem("albums", R.string.nav_albums, R.drawable.ic_album_24)
    object Artists : NavBarItem("artists", R.string.nav_artists, R.drawable.ic_artist_24)
    object Collectors : NavBarItem("collectors", R.string.nav_collectors, R.drawable.ic_collector_24)
}

private val navBarItems = listOf(
    NavBarItem.Albums,
    NavBarItem.Artists,
    NavBarItem.Collectors,
)

@Composable
fun NavContent(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "albums"
    ) {
        composable(route = "albums") { AlbumListScreen() }
        composable(route = "artists") { ArtistListScreen() }
        composable(route = "collectors") { CollectorListScreen() }
    }
}

@Composable
fun NavBar(navController: NavHostController, currentBackStackEntry: NavBackStackEntry?) {
    val route = currentBackStackEntry?.destination?.route

    NavigationBar {
        navBarItems.forEach { item ->
            NavigationBarItem(
                selected = route == item.route,
                label = { Text(stringResource(item.stringId)) },
                icon = { Icon(painterResource(item.iconId), contentDescription = null) },
                onClick = {
                    if (item.route == route) return@NavigationBarItem

                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        navController.graph.setStartDestination(item.route)

                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
