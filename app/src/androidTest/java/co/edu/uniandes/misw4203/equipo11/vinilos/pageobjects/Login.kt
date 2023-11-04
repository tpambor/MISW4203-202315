package co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import co.edu.uniandes.misw4203.equipo11.vinilos.MainActivity

class Login (private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) : PageObject(composeTestRule) {
    fun getCollectorButton(): SemanticsNodeInteraction {
        return composeTestRule.onNodeWithText("Coleccionista")
    }

    fun getVisitorButton(): SemanticsNodeInteraction {
        return composeTestRule.onNodeWithText("Visitante")
    }
}