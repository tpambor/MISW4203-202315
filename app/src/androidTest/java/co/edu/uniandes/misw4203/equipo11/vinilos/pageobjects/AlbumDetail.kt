package co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects

import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity
class AlbumDetail (composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) : PageObject(composeTestRule) {

    fun getAlbumDescription(): SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("album-description")
        )
    }

    fun getDataAlbumext() : SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("data-albumDetail")
        )
    }

    fun getDataDescriptionAlbumText() : SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("description-albumDetail")
        )
    }

    fun getPerformesAlbumText() : SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("performer-list")
        )
    }
    fun getHeaderAlbumText() : SemanticsNodeInteraction {
        return findExactlyOne(
            hasTestTag("albums-header")
        )
    }


    fun getAlbumDetail() : Boolean {
        try {
            getAlbumDescription()
            getDataAlbumext()
            getDataDescriptionAlbumText()
            getPerformesAlbumText()
        } catch (ex: ComposeTimeoutException) {
            return false
        }
        return true
    }


}