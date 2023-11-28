package co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects

import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity

class CollectorDetail(composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) : PageObject(composeTestRule) {
    private fun getHeader(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("collector-detail-header")
        )
    }

    fun getCollectorDetail() : Boolean {
        try {
            getHeader()
        } catch (ex: ComposeTimeoutException) {
            return false
        }
        return true
    }

    fun selectArtistsTab(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasText("Artistas favoritos").and(hasAnyAncestor(hasTestTag("collector-detail-tabs")))
        )
    }

    fun selectAlbumsTab(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasText("Álbumes").and(hasAnyAncestor(hasTestTag("collector-detail-tabs")))
        )
    }

    fun getArtistList(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("collector-detail-artist-list")
        )
    }

    fun getAlbumsList(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("album-list")
        )
    }
}
