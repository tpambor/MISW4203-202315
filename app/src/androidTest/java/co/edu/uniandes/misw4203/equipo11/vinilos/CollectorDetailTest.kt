package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.CollectorDetail
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.CollectorList
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.Login
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.NavBar
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class CollectorDetailTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun showsCollectorDetailArtistsVisitor() {
        // Given I login as a visitor
        val login = Login(composeTestRule)
        login.getVisitorButton().performClick()

        // When I click on the collectors tab
        val navbar = NavBar(composeTestRule)
        navbar.getCollectorButton().assertIsDisplayed().performClick()

        // Then I see a list of all collectors
        val collectorList = CollectorList(composeTestRule)
        assert(collectorList.getCollectors().fetchSemanticsNodes().isNotEmpty())

        // When I click on some collector card
        val list = collectorList.getCollectors()
        val collector = list[1]
        collector.performClick()

        // Then I see the details of the collector
        val collectorDetail = CollectorDetail(composeTestRule)
        assert(collectorDetail.getCollectorDetail())

        // When I click on the "Artistas favoritos" tab
        collectorDetail.selectArtistsTab().performClick()

        // Then I see the list of artists
        assertNotNull(collectorDetail.getArtistList().fetchSemanticsNode())
    }

    @Test
    fun showsCollectorDetailAlbumsVisitor() {
        // Given I login as a visitor
        val login = Login(composeTestRule)
        login.getVisitorButton().performClick()

        // When I click on the collectors tab
        val navbar = NavBar(composeTestRule)
        navbar.getCollectorButton().assertIsDisplayed().performClick()

        // Then I see a list of all collectors
        val collectorList = CollectorList(composeTestRule)
        assert(collectorList.getCollectors().fetchSemanticsNodes().isNotEmpty())

        // When I click on some collector card
        val list = collectorList.getCollectors()
        val collector = list[1]
        collector.performClick()

        // Then I see the details of the collector
        val collectorDetail = CollectorDetail(composeTestRule)
        assert(collectorDetail.getCollectorDetail())

        // When I click on the "Albums" tab
        collectorDetail.selectAlbumsTab().performClick()

        // Then I see the list of albums
        assertNotNull(collectorDetail.getAlbumsList().fetchSemanticsNode())
    }

    @Test
    fun showsCollectorDetailArtistsCollector() {
        // Given I login as a visitor
        val login = Login(composeTestRule)
        login.getCollectorButton().performClick()

        // When I click on the collectors tab
        val navbar = NavBar(composeTestRule)
        navbar.getCollectorButton().assertIsDisplayed().performClick()

        // Then I see a list of all collectors
        val collectorList = CollectorList(composeTestRule)
        assert(collectorList.getCollectors().fetchSemanticsNodes().isNotEmpty())

        // When I click on some collector card
        val list = collectorList.getCollectors()
        val collector = list[1]
        collector.performClick()

        // Then I see the details of the collector
        val collectorDetail = CollectorDetail(composeTestRule)
        assert(collectorDetail.getCollectorDetail())

        // When I click on the "Artistas favoritos" tab
        collectorDetail.selectArtistsTab().performClick()

        // Then I see the list of artists
        assertNotNull(collectorDetail.getArtistList().fetchSemanticsNode())
    }

    @Test
    fun showsCollectorDetailAlbumsCollector() {
        // Given I login as a visitor
        val login = Login(composeTestRule)
        login.getCollectorButton().performClick()

        // When I click on the collectors tab
        val navbar = NavBar(composeTestRule)
        navbar.getCollectorButton().assertIsDisplayed().performClick()

        // Then I see a list of all collectors
        val collectorList = CollectorList(composeTestRule)
        assert(collectorList.getCollectors().fetchSemanticsNodes().isNotEmpty())

        // When I click on some collector card
        val list = collectorList.getCollectors()
        val collector = list[1]
        collector.performClick()

        // Then I see the details of the collector
        val collectorDetail = CollectorDetail(composeTestRule)
        assert(collectorDetail.getCollectorDetail())

        // When I click on the "Albums" tab
        collectorDetail.selectAlbumsTab().performClick()

        // Then I see the list of albums
        assertNotNull(collectorDetail.getAlbumsList().fetchSemanticsNode())
    }
}
