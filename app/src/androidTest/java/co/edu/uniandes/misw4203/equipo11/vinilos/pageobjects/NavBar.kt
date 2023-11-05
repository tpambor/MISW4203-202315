package co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import co.edu.uniandes.misw4203.equipo11.vinilos.MainActivity

class NavBar(composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) : PageObject(composeTestRule) {
    fun getAlbumButton(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasText("Álbumes").and(hasAnyAncestor(hasTestTag("navbar")))
        )
    }

    fun getArtistButton(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasText("Artistas").and(hasAnyAncestor(hasTestTag("navbar")))
        )
    }

    fun getCollectorButton(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasText("Coleccionistas").and(hasAnyAncestor(hasTestTag("navbar")))
        )
    }

    fun getLogoutButton(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasText("Salir").and(hasAnyAncestor(hasTestTag("navbar")))
        )
    }
}
