package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.AlbumList
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.NavBar
import org.junit.Rule
import org.junit.Test

class AlbumListTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun showsAlbums() {
        // Given I have opened the Vinilos App
        // When I click on the Album button in the navigation bar
        val navbar = NavBar(composeTestRule)
        val button = navbar.getAlbumButton()
        button.assertIsDisplayed()
        button.performClick()

        // Then I see a list of all albums
        val albumList = AlbumList(composeTestRule)
        assert(albumList.getAlbums().fetchSemanticsNodes().size > 1)
    }
}
