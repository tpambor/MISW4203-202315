package co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import co.edu.uniandes.misw4203.equipo11.vinilos.MainActivity

class ArtistList(composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) : PageObject(composeTestRule) {
    fun getArtists(): SemanticsNodeInteractionCollection {
        return findAtLeastOne(
            hasTestTag("performer-list-item")
        )
    }

    // Method to select "músicos" Tab
    fun selectMusiciansTab(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("Músicos")
        )
    }

}