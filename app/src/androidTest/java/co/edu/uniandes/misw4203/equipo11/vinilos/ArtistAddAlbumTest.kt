package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.AlbumList
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.ArtistDetail
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.ArtistList
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.ConfirmationDialog
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.Login
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.NavBar
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity
import org.junit.Rule
import org.junit.Test

class ArtistAddAlbumTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addAlbumToMusician() {
        // Given I login as a Collector
        val login = Login(composeTestRule)
        login.getCollectorButton().performClick()

        // When I go to Artist List section and musicians subsection
        val navbar = NavBar(composeTestRule)
        navbar.getArtistButton().assertIsDisplayed().performClick()

        val artistList = ArtistList(composeTestRule)
        artistList.selectMusiciansTab().performClick()
        assert(artistList.getArtists().fetchSemanticsNodes().isNotEmpty())

        // And I click on some musician
        val list = artistList.getArtists()
        val musician = list[0]
        musician.performClick()

        // And I click the button to add a album
        val artistDetail = ArtistDetail(composeTestRule)
        artistDetail.getAddAlbumButton().assertIsDisplayed().performClick()

        // And I click on some album
        val albumList = AlbumList(composeTestRule)
        val candidates = albumList.getAlbums()
        val album = candidates[0]
        album.performClick()

        // And I confirm that I want to add the album to the musician
        val confirmationDialog = ConfirmationDialog(composeTestRule)
        confirmationDialog.getConfirmButton().assertIsDisplayed().performClick()

        // Then I see a message that the album has been added to the musician successfully
        artistDetail.findExactlyOne(
            hasText("Álbum agregado a músico")
        ).assertIsDisplayed()
    }

    @Test
    fun addAlbumToBand() {
        // Given I login as a Collector
        val login = Login(composeTestRule)
        login.getCollectorButton().performClick()

        // When I go to Artist List section and bands subsection
        val navbar = NavBar(composeTestRule)
        navbar.getArtistButton().assertIsDisplayed().performClick()

        val artistList = ArtistList(composeTestRule)
        artistList.selectBandsTab().performClick()
        assert(artistList.getArtists().fetchSemanticsNodes().isNotEmpty())

        // And I click on some band
        val list = artistList.getArtists()
        val band = list[0]
        band.performClick()

        // And I click the button to add a album
        val artistDetail = ArtistDetail(composeTestRule)
        artistDetail.getAddAlbumButton().assertIsDisplayed().performClick()

        // And I click on some album
        val albumList = AlbumList(composeTestRule)
        val candidates = albumList.getAlbums()
        val album = candidates[0]
        album.performClick()

        // And I confirm that I want to add the album to the band
        val confirmationDialog = ConfirmationDialog(composeTestRule)
        confirmationDialog.getConfirmButton().assertIsDisplayed().performClick()

        // Then I see a message that the album has been added to the band successfully
        artistDetail.findExactlyOne(
            hasText("Álbum agregado a banda")
        ).assertIsDisplayed()
    }
}
