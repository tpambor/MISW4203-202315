package co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects

import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity

class ArtistDetail (composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) : PageObject(composeTestRule) {

    private fun getArtistDescription(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("artist-description")
        )
    }

    private fun getBirthDateText() : SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("birthdate-text")
        ).assertIsDisplayed()
    }
    private fun getAlbumsHeader(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("albums-header")
        )
    }

    private fun getMembersHeader(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("members-header")
        )
    }
    fun getMusicianDetail() : Boolean {
        try {
            getArtistDescription()
            getBirthDateText().assertTextEquals("Nacimiento:")
            getAlbumsHeader()
        } catch (ex: ComposeTimeoutException) {
            return false
        }
        return true
    }

    fun getBandDetail() : Boolean {
        try {
            getArtistDescription()
            getBirthDateText().assertTextEquals("Fecha de creación:")
            getMembersHeader()
            getAlbumsHeader()
        } catch (ex: ComposeTimeoutException) {
            return false
        }
        return true
    }
}