package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.ArtistDetail
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.ArtistList
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.Login
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.NavBar
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity
import org.junit.Rule
import org.junit.Test

class ArtistDetailTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun clickAndShowListMusicians(navbar: NavBar, artistList: ArtistList) {
        navbar.getArtistButton().assertIsDisplayed().performClick()
        artistList.selectMusiciansTab().performClick()
        assert(artistList.getArtists().fetchSemanticsNodes().isNotEmpty())
    }

    private fun clickAndShowListBands(navbar: NavBar, artistList: ArtistList) {
        navbar.getArtistButton().assertIsDisplayed().performClick()
        artistList.selectBandsTab().performClick()
        assert(artistList.getArtists().fetchSemanticsNodes().isNotEmpty())
    }

    @Test
    fun showsMusicianDetailCollector() {
        // Given I login as a Collector, go to Artist List section and "Músicos" subsection
        val login = Login(composeTestRule)
        login.getCollectorButton().performClick()

        val navbar = NavBar(composeTestRule)
        val artistList = ArtistList(composeTestRule)
        clickAndShowListMusicians(navbar, artistList)

        // When I click on some Musician Card
        val list = artistList.getArtists()
        val musician = list[0]
        musician.performClick()

        // Then I see Musician Detail
        val artistDetail = ArtistDetail(composeTestRule)
        assert(artistDetail.getMusicianDetail())
    }

    @Test
    fun showsMusicianDetailVisitor() {
        // Given I login as a Collector, go to Artist List section and "Músicos" subsection
        val login = Login(composeTestRule)
        login.getVisitorButton().performClick()

        val navbar = NavBar(composeTestRule)
        val artistList = ArtistList(composeTestRule)
        clickAndShowListMusicians(navbar, artistList)

        // When I click on some Musician Card
        val list = artistList.getArtists()
        val musician = list[0]
        musician.performClick()

        // Then I see Musician Detail
        val artistDetail = ArtistDetail(composeTestRule)
        assert(artistDetail.getMusicianDetail())
    }

    @Test
    fun showsBandDetailCollector() {
        // Given I login as a Collector, go to Artist List section and "Bandas" subsection
        val login = Login(composeTestRule)
        login.getCollectorButton().performClick()

        val navbar = NavBar(composeTestRule)
        val artistList = ArtistList(composeTestRule)
        clickAndShowListBands(navbar, artistList)

        // When I click on some Musician Card
        val list = artistList.getArtists()
        val band = list[0]
        band.performClick()

        // Then I see Musician Detail
        val artistDetail = ArtistDetail(composeTestRule)
        assert(artistDetail.getBandDetail())
    }

    @Test
    fun showsBandDetailVisitor() {
        // Given I login as a Visitor, go to Artist List section and "Bandas" subsection
        val login = Login(composeTestRule)
        login.getVisitorButton().performClick()

        val navbar = NavBar(composeTestRule)
        val artistList = ArtistList(composeTestRule)
        clickAndShowListBands(navbar, artistList)

        // When I click on some Musician Card
        val list = artistList.getArtists()
        val band = list[0]
        band.performClick()

        // Then I see Musician Detail
        val artistDetail = ArtistDetail(composeTestRule)
        assert(artistDetail.getBandDetail())
    }
}