package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.AlbumList
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.ArtistList
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.Login
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.NavBar
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity
import org.junit.Rule
import org.junit.Test

class NavigationTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun loginViewListsLogout() {
        // Given I login as a collector
        val login = Login(composeTestRule)
        login.getCollectorButton().performClick()

        val navbar = NavBar(composeTestRule)
        val albumList = AlbumList(composeTestRule)
        val artistList = ArtistList(composeTestRule)

        // When I click on the Album button in the navigation bar
        navbar.getAlbumButton().assertIsDisplayed().performClick()
        assert(albumList.getAlbums().fetchSemanticsNodes().isNotEmpty())

        // And I click on the Artist button in the navigation bar
        navbar.getArtistButton().assertIsDisplayed().performClick()
        assert(artistList.getArtists().fetchSemanticsNodes().isNotEmpty())

        // And I click on Logout in the navigation bar
        navbar.getLogoutButton().assertIsDisplayed().performClick()

        // Then I can see the login screen
        login.getVisitorButton().assertIsDisplayed()
        login.getCollectorButton().assertIsDisplayed()
    }
}
