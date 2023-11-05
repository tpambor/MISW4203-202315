package co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects

import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import co.edu.uniandes.misw4203.equipo11.vinilos.MainActivity

class CollectorList(composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) : PageObject(composeTestRule) {
    fun getCollectors(): SemanticsNodeInteractionCollection {
        return findAtLeastOne(
            hasTestTag("collector-list-item")
        )
    }

    fun getUserCollector(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("collector-list-item-user")
        )
    }

    fun hasUserCollector(): Boolean {
        try {
            getUserCollector()
        } catch (ex: ComposeTimeoutException) {
            return false
        }

        return true
    }
}
