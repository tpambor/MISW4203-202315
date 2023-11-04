package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.ArtistList
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.Login
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.NavBar
import org.junit.Rule
import org.junit.Test

class ArtistListTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun clickAndShowListMusicians(navbar: NavBar, artistList: ArtistList) {
        // When I click on the "Músicos" tab
        navbar.getArtistButton().assertIsDisplayed().performClick()

        // Then I see a list of all musicians
        artistList.selectMusiciansTab().performClick()
        assert(artistList.getArtists().fetchSemanticsNodes().isNotEmpty())
    }

    private fun clickAndShowListBands(navbar: NavBar, artistList: ArtistList) {
        // When I click on the "Bandas" tab
        navbar.getArtistButton().assertIsDisplayed().performClick()

        // Then I see a list of all bands
        artistList.selectBandsTab().performClick()
        assert(artistList.getArtists().fetchSemanticsNodes().isNotEmpty())
    }

    @Test
    fun showsMusicians() {
        // Given I login as a "Coleccionista"
        val login = Login(composeTestRule)
        login.getColeccionistaButton().performClick()

        // When - Then explained in clickAndShowListMusicians function
        val navbar = NavBar(composeTestRule)
        val artistList = ArtistList(composeTestRule)
        clickAndShowListMusicians(navbar, artistList)

        // And I can see Fav buttons
        assert(artistList.getFavButton().fetchSemanticsNodes().isNotEmpty())

        // I Logout
        navbar.getLogoutButton().assertIsDisplayed().performClick()

        // Given I login as a "Visitante"
        login.getVisitanteButton().performClick()

        // When - Then explained in clickAndShowListMusicians function
        clickAndShowListMusicians(navbar, artistList)

        // And I can't see Fav buttons
        assert(artistList.missingFavButton().fetchSemanticsNodes().isNullOrEmpty())
    }

    @Test
    fun showsBands() {
        // Given I login as a "Coleccionista"
        val login = Login(composeTestRule)
        login.getColeccionistaButton().performClick()

        // When - Then explained in clickAndShowListBands function
        val navbar = NavBar(composeTestRule)
        val artistList = ArtistList(composeTestRule)

        clickAndShowListBands(navbar, artistList)

        // And I can see Fav buttons
        assert(artistList.getFavButton().fetchSemanticsNodes().isNotEmpty())

        // I Logout
        navbar.getLogoutButton().assertIsDisplayed().performClick()

        // Given I login as a "Visitante"
        login.getVisitanteButton().performClick()

        // When - Then explained in clickAndShowListBands function
        clickAndShowListBands(navbar, artistList)

        // And I can't see Fav buttons
        assert(artistList.missingFavButton().fetchSemanticsNodes().isNullOrEmpty())
    }
}
