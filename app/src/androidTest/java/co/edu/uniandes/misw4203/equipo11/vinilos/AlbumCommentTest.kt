package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.AlbumComment
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.AlbumDetail
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.AlbumList
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.Login
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.NavBar
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

class AlbumCommentTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun showsAlbumDetailCollector() {
        // Given I login as a collector
        val login = Login(composeTestRule)
        login.getCollectorButton().performClick()

        // When I go to the album list
        val navbar = NavBar(composeTestRule)
        navbar.getAlbumButton().assertIsDisplayed().performClick()

        // When I select a album
        val albumList = AlbumList(composeTestRule)
        assert(albumList.getAlbums().fetchSemanticsNodes().isNotEmpty())
        val list = albumList.getAlbums()
        val album = list[1]
        album.performClick()

        // And I click on the add album button
        val albumDetail = AlbumDetail(composeTestRule)
        albumDetail.getAddCommentButton().assertIsDisplayed().performClick()

        // And I select the rating
        val albumComment = AlbumComment(composeTestRule)
        albumComment.setRating(Random.nextInt(1, 5))

        // And I write a comment
        val comment = "E2E: " + java.util.UUID.randomUUID().toString()
        albumComment.fillComment(comment)

        // And I click the submit button
        albumComment.getSubmitButton().assertIsDisplayed().performClick()

        // Then I see a message that the comment has been added successfully
        albumComment.findExactlyOne(
            hasText("Comentario agregado exitosamente")
        ).assertIsDisplayed()

        // And I see the comment in the list of comments of the album
        albumDetail.getCommentWithText(comment)
    }
}
