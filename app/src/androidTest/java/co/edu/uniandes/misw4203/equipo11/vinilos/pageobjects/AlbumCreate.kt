package co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity

class AlbumCreate(private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) : PageObject(composeTestRule) {
    fun fillBasicInput(text: String, textTag: String) {
        findExactlyOne(
            hasTestTag(textTag)
        ).performClick().performTextInput(text)
    }

    fun selectOption(textTag: String, indexOption: Int) {
        findExactlyOne(
            hasTestTag(textTag)
        ).performClick()
        findExactlyOne(
            hasTestTag("$textTag-$indexOption")
        ).performClick()
    }


    fun getSubmitButton() : SemanticsNodeInteraction {
        composeTestRule.onNodeWithTag("album-submit").performScrollTo()
        return findExactlyOne(
            hasTestTag("album-submit")
        )
    }
}