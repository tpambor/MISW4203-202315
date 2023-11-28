package co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects

import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performScrollToNode
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

    fun getAddCommentButton() : SemanticsNodeInteraction {
        findExactlyOne(
            hasTestTag("album-detail-list")
        ).performScrollToNode(
            hasTestTag("add-comment")
        )

        return findExactlyOne(
            hasTestTag("add-comment")
        )
    }

    fun getCommentWithText(text: String) : SemanticsNodeInteraction {
        findExactlyOne(
            hasTestTag("album-detail-list")
        ).performScrollToNode(
            hasText(text)
        )

        return findExactlyOne(
            hasText(text)
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
