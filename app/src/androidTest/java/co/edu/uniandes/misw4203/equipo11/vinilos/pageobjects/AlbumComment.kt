package co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity

class AlbumComment(composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) : PageObject(composeTestRule) {
    fun fillComment(text: String) {
        findExactlyOne(
            hasTestTag("comment-comment")
        ).performClick().performTextInput(text)
    }

    fun setRating(rating: Int) {
        findExactlyOne(
            hasTestTag("comment-rating-$rating")
        ).performClick()
    }

    fun getSubmitButton() : SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("comment-submit")
        )
    }
}
