package co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity

class AlbumList(composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) : PageObject(composeTestRule) {
    fun getAlbums(): SemanticsNodeInteractionCollection {
        return findAtLeastOne(
            hasTestTag("album-list-item")
        )
    }

    fun getAddAlbumButton(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("add-album-button")
        )
    }
}
