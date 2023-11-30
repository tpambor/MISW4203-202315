package co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity

class AlbumTrack(composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) : PageObject(composeTestRule) {
    fun fillName(text: String) {
        findExactlyOne(
            hasTestTag("track-name")
        ).performClick().performTextInput(text)
    }

    fun fillDuration(text: String) {
        findExactlyOne(
            hasTestTag("track-duration")
        ).performClick().performTextInput(text)
    }

    fun getSubmitButton() : SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("track-submit")
        )
    }
}
