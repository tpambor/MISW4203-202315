package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.ArtistList
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.NavBar
import org.junit.Rule
import org.junit.Test

class ArtistListTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun showsMusicians() {
        // Given I have opened the Vinilos App
        // When I click on the Artist button in the navigation bar
        val navbar = NavBar(composeTestRule)
        val button = navbar.getArtistButton()
        button.assertIsDisplayed()
        button.performClick()

        // Then I select "Músicos" Tab and see a list of all musicians
        val artistList = ArtistList(composeTestRule)
        artistList.selectMusiciansTab().performClick()
        assert(artistList.getArtists().fetchSemanticsNodes().isNotEmpty())
    }
    @Test
    fun showsBands() {
        // Given I have opened the Vinilos App
        // When I click on the Artist button in the navigation bar
        val navbar = NavBar(composeTestRule)
        val button = navbar.getArtistButton()
        button.assertIsDisplayed()
        button.performClick()

        // Then I select "Bandas" Tab and see a list of all bands
        val artistList = ArtistList(composeTestRule)
        artistList.selectBandsTab().performClick()
        assert(artistList.getArtists().fetchSemanticsNodes().isNotEmpty())
    }
}
