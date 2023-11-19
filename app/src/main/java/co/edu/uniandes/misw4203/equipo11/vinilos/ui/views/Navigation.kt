package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import co.edu.uniandes.misw4203.equipo11.vinilos.R

sealed class NavBarItem(val route: String, @StringRes val stringId: Int, @DrawableRes val iconId: Int) {
    data object Albums : NavBarItem("albums", R.string.nav_albums, R.drawable.ic_album_24)
    data object Artists : NavBarItem("artists", R.string.nav_artists, R.drawable.ic_artist_24)
    data object Collectors : NavBarItem("collectors", R.string.nav_collectors, R.drawable.ic_collector_24)
    data object Login : NavBarItem("login", R.string.nav_login, R.drawable.ic_leave_24)
}

private val navBarItems = listOf(
    NavBarItem.Albums,
    NavBarItem.Artists,
    NavBarItem.Collectors,
    NavBarItem.Login
)

@Composable
fun NavContent(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable(route = "login") { LoginScreen(navController) }
        composable(route = "albums") { AlbumListScreen(snackbarHostState, navController) }
        composable(route = "artists") { ArtistListScreen(snackbarHostState, navController) }
        composable(route = "collectors") { CollectorListScreen(snackbarHostState) }
        composable(
            route = "albums/{albumId}",
            arguments = listOf(navArgument("albumId") { type = NavType.IntType })
        ){ backStackEntry ->
            AlbumDetailScreen(snackbarHostState, requireNotNull(backStackEntry.arguments).getInt("albumId"), navController)
        }
        composable(
            route = "artists/musician/{artistId}",
            arguments = listOf(navArgument("artistId") { type = NavType.IntType })
        ){ backStackEntry ->
            MusicianDetailScreen(snackbarHostState, requireNotNull(backStackEntry.arguments).getInt("artistId"), navController)
        }
        composable(
            route = "artists/band/{artistId}",
            arguments = listOf(navArgument("artistId") { type = NavType.IntType })
        ){ backStackEntry ->
            BandDetailScreen(snackbarHostState, requireNotNull(backStackEntry.arguments).getInt("artistId"), navController)
        }
    }
}

@Composable
fun NavBar(navController: NavHostController, currentBackStackEntry: NavBackStackEntry?) {
    val route = currentBackStackEntry?.destination?.route

    // Do not display NavigationBar for login screen
    if (route == "login")
        return

    NavigationBar(
        modifier = Modifier.testTag("navbar")
    ) {
        navBarItems.forEach { item ->
            NavigationBarItem(
                selected = route?.startsWith(item.route) ?: false,
                label = { Text(stringResource(item.stringId), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                icon = { Icon(painterResource(item.iconId), contentDescription = null) },
                onClick = {
                        if (item.route == route) return@NavigationBarItem

                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(navController: NavHostController, currentBackStackEntry: NavBackStackEntry?) {
    val route = currentBackStackEntry?.destination?.route

    val title = when (route) {
        "artists/musician/{artistId}" -> stringResource(R.string.top_nav_artist)
        "artists/band/{artistId}" -> stringResource(R.string.top_nav_artist)
        "albums/{albumId}" -> stringResource(R.string.top_nav_album)
        else -> ""
    }

    val visible = !(
        route == "login" ||
        route == "albums" ||
        route == "artists" ||
        route == "collectors"
    )

    AnimatedVisibility(visible) {
        TopAppBar(
            title = { Text(text = title) },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigateUp()}
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )
    }
}