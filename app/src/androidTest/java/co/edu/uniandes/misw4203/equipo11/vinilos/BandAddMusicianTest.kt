package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.ArtistDetail
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.ArtistList
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.ConfirmationDialog
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.Login
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.NavBar
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity
import org.junit.Rule
import org.junit.Test

class BandAddMusicianTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addBandMember() {
        // Given I login as a Collector
        val login = Login(composeTestRule)
        login.getCollectorButton().performClick()

        // When I go to Artist List section and "Bandas" subsection
        val navbar = NavBar(composeTestRule)
        navbar.getArtistButton().assertIsDisplayed().performClick()

        val artistList = ArtistList(composeTestRule)
        artistList.selectBandsTab().performClick()
        assert(artistList.getArtists().fetchSemanticsNodes().isNotEmpty())

        // And I click on some band
        val list = artistList.getArtists()
        val band = list[0]
        band.performClick()

        // And I click the button to add a band member
        val artistDetail = ArtistDetail(composeTestRule)
        artistDetail.getAddMemberButton().assertIsDisplayed().performClick()

        // And I click on some musician
        val candidates = artistList.getArtists()
        val musician = candidates[0]
        musician.performClick()

        // And I confirm that I want to add the musician to the band
        val confirmationDialog = ConfirmationDialog(composeTestRule)
        confirmationDialog.getConfirmButton().assertIsDisplayed().performClick()

        // Then I see a message that the comment has been added successfully
        artistDetail.findExactlyOne(
            hasText("Músico agregado a banda")
        ).assertIsDisplayed()
    }
}
