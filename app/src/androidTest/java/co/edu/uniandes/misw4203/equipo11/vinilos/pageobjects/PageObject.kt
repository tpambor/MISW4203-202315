package co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import co.edu.uniandes.misw4203.equipo11.vinilos.MainActivity

abstract class PageObject(private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) {
    fun findExactlyOne(matcher: SemanticsMatcher): SemanticsNodeInteraction {
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(matcher).fetchSemanticsNodes().size == 1
        }

        return composeTestRule.onNode(matcher)
    }

    fun findAtLeastOne(matcher: SemanticsMatcher): SemanticsNodeInteractionCollection {
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(matcher).fetchSemanticsNodes().isNotEmpty()
        }

        return composeTestRule.onAllNodes(matcher)
    }

    fun findNoMatches(matcher: SemanticsMatcher): SemanticsNodeInteractionCollection {
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(matcher).fetchSemanticsNodes().isEmpty()
        }

        return composeTestRule.onAllNodes(matcher)
    }
}
