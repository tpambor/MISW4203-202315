package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.AlbumDetail
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.AlbumList
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.AlbumTrack
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.Login
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.NavBar
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity
import org.junit.Rule
import org.junit.Test

class AlbumTrackTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addTrack() {
        // Given I login as a collector
        val login = Login(composeTestRule)
        login.getCollectorButton().performClick()

        // When I go to the album list
        val navbar = NavBar(composeTestRule)
        navbar.getAlbumButton().assertIsDisplayed().performClick()

        // When I select a album
        val albumList = AlbumList(composeTestRule)
        assert(albumList.getAlbums().fetchSemanticsNodes().isNotEmpty())
        val list = albumList.getAlbums()
        val album = list[1]
        album.performClick()

        // And I click on the add track button
        val albumDetail = AlbumDetail(composeTestRule)
        albumDetail.getAddTrackButton().assertIsDisplayed().performClick()

        // And I write a name
        val albumTrack = AlbumTrack(composeTestRule)
        val name = "E2E: " + java.util.UUID.randomUUID().toString()
        albumTrack.fillName(name)

        // And I write a duration
        val duration = "2:34"
        albumTrack.fillDuration(duration)

        // And I click the submit button
        albumTrack.getSubmitButton().assertIsDisplayed().performClick()

        // Then I see a message that the track has been added successfully
        albumTrack.findExactlyOne(
            hasText("Track agregado exitosamente")
        ).assertIsDisplayed()

        // And I see the track in the list of tracks of the album
        albumDetail.getTrackWithName(name)
    }
}
