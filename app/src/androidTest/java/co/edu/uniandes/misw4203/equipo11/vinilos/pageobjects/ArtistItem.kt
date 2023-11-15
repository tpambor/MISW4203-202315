package co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.performClick

class ArtistItem(private val node: SemanticsNodeInteraction) {
    fun isNotFavorite(): Boolean {
        return node.onChildren().fetchSemanticsNodes()[0]
            .config.getOrNull(SemanticsProperties.TestTag) == "performer-fav-button-unchecked"
    }

    fun isFavorite(): Boolean {
        return node.onChildren().fetchSemanticsNodes()[0]
            .config.getOrNull(SemanticsProperties.TestTag) == "performer-fav-button-checked"
    }

    fun clickFavorite() {
        node.onChildren()[0].performClick()
    }
}