package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.CollectorList
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.Login
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.NavBar
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CollectorListTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun showsCollectorsVisitor() {
        // Given I login as a visitor
        val login = Login(composeTestRule)
        login.getVisitorButton().performClick()

        // When I click on the collectors tab
        val navbar = NavBar(composeTestRule)
        navbar.getCollectorButton().assertIsDisplayed().performClick()

        // Then I see a list of all collectors
        val collectorList = CollectorList(composeTestRule)
        assert(collectorList.getCollectors().fetchSemanticsNodes().isNotEmpty())

        // And I do not see my own account at the beginning because I am only a visitor
        assertFalse(collectorList.hasUserCollector())
    }

    @Test
    fun showsCollectorsCollector() {
        // Given I login as a collector
        val login = Login(composeTestRule)
        login.getCollectorButton().performClick()

        // When I click on the collectors tab
        val navbar = NavBar(composeTestRule)
        navbar.getCollectorButton().assertIsDisplayed().performClick()

        // Then I see a list of all collectors
        val collectorList = CollectorList(composeTestRule)
        assert(collectorList.getCollectors().fetchSemanticsNodes().isNotEmpty())

        // And I see my own account at the beginning
        assertTrue(collectorList.hasUserCollector())
    }
}
