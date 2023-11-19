package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.AlbumDetail
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.AlbumList
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.Login
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.NavBar
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity
import org.junit.Rule
import org.junit.Test

class AlbumDetailTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun clickAndShowListAlbum(navbar: NavBar, albumList: AlbumList) {
        navbar.getAlbumButton().assertIsDisplayed().performClick()
        assert(albumList.getAlbums().fetchSemanticsNodes().isNotEmpty())
    }

    @Test
    fun showsAlbumDetailCollector() {
        // Given I login as a collector and go to the album list
        val login = Login(composeTestRule)
        login.getCollectorButton().performClick()

        val navbar = NavBar(composeTestRule)
        val albumList = AlbumList(composeTestRule)
        clickAndShowListAlbum(navbar, albumList)

        // When I click on some album card
        val list = albumList.getAlbums()
        val album = list[1]
        album.performClick()

        // Then I see the details of the album
        val albumDetail = AlbumDetail(composeTestRule)
        assert(albumDetail.getAlbumDetail())
    }


    @Test
    fun showsAlbumDetailVisitor() {
        // Given I login as a visitor and go to the album list
        val login = Login(composeTestRule)
        login.getVisitorButton().performClick()

        val navbar = NavBar(composeTestRule)
        val albumList = AlbumList(composeTestRule)
        clickAndShowListAlbum(navbar, albumList)

        // When I click on some album card
        val list = albumList.getAlbums()
        val album = list[1]
        album.performClick()

        // Then I see the details of the album
        val albumDetail = AlbumDetail(composeTestRule)
        assert(albumDetail.getAlbumDetail())
    }
}
