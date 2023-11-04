package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.AlbumList
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.Login
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.NavBar
import org.junit.Rule
import org.junit.Test

class AlbumListTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun clickAndShowListAlbum(navbar: NavBar, albumList: AlbumList) {
        // When I click on the Album button in the navigation bar
        navbar.getAlbumButton().assertIsDisplayed().performClick()

        // Then I see a list of all albums
        assert(albumList.getAlbums().fetchSemanticsNodes().isNotEmpty())
    }
    @Test
    fun showsAlbums() {
        // Given I login as a "Coleccionista"
        val login = Login(composeTestRule)
        login.getColeccionistaButton().performClick()

        // When - Then explained in clickAndShowListAlbum function
        val navbar = NavBar(composeTestRule)
        val albumList = AlbumList(composeTestRule)

        clickAndShowListAlbum(navbar, albumList)

        // I Logout
        navbar.getLogoutButton().assertIsDisplayed().performClick()

        // Given I login as a "Visitante"
        login.getVisitanteButton().performClick()

        // When - Then explained in clickAndShowListAlbum function
        clickAndShowListAlbum(navbar, albumList)
    }
}
