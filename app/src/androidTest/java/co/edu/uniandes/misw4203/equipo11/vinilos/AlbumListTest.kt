package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.AlbumList
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.Login
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.NavBar
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity
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
    fun showsAlbumsCollector() {
        // Given I login as a collector
        val login = Login(composeTestRule)
        login.getCollectorButton().performClick()

        // When - Then explained in clickAndShowListAlbum function
        val navbar = NavBar(composeTestRule)
        val albumList = AlbumList(composeTestRule)
        clickAndShowListAlbum(navbar, albumList)
    }

    @Test
    fun showsAlbumsVisitor() {
        // Given I login as a visitor
        val login = Login(composeTestRule)
        login.getVisitorButton().performClick()

        composeTestRule.waitForIdle()

        // When - Then explained in clickAndShowListAlbum function
        val navbar = NavBar(composeTestRule)
        val albumList = AlbumList(composeTestRule)
        clickAndShowListAlbum(navbar, albumList)
    }
}
