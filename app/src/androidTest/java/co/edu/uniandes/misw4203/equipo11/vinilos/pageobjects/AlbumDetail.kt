package co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects

import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity

class AlbumDetail (composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) : PageObject(composeTestRule) {

    private fun getAlbumCover(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("album-detail-cover")
        )
    }

    private fun getAlbumInfo() : SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("album-detail-info")
        )
    }

    private fun getAlbumDescription() : SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("album-detail-description")
        )
    }

    fun getAlbumDetail() : Boolean {
        try {
            getAlbumCover()
            getAlbumInfo()
            getAlbumDescription()
        } catch (ex: ComposeTimeoutException) {
            return false
        }
        return true
    }
}
