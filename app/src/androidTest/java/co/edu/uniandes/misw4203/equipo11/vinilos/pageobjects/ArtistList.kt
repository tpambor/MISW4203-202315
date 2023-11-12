package co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects

import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity

class ArtistList(composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) : PageObject(composeTestRule) {
    // Method to get all artists
    fun getArtists(): SemanticsNodeInteractionCollection {
        return findAtLeastOne(
            hasTestTag("performer-list-item")
        )
    }

    private fun getFavButton(): SemanticsNodeInteractionCollection {
        return findAtLeastOne(
            hasTestTag("performer-fav-button")
        )
    }

    // Method to select "músicos" Tab
    fun selectMusiciansTab(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("Músicos")
        )
    }

    // Method to select "bandas" Tab
    fun selectBandsTab(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("Bandas")
        )
    }

    fun hasFavButtons(): Boolean {
        try {
            getFavButton()
        } catch (ex: ComposeTimeoutException) {
            return false
        }

        return true
    }

}