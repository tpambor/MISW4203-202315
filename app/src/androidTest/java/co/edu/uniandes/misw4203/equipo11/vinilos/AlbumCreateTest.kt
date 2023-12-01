package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.AlbumCreate
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.AlbumList
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.Login
import co.edu.uniandes.misw4203.equipo11.vinilos.pageobjects.NavBar
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity
import org.junit.Rule
import org.junit.Test

class AlbumCreateTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun createAlbumDetailCollector() {
        // Given I login as a collector
        val login = Login(composeTestRule)
        login.getCollectorButton().performClick()

        // When I go to the album list
        val navbar = NavBar(composeTestRule)
        navbar.getAlbumButton().assertIsDisplayed().performClick()

        // And I click on the add album button
        val albumList = AlbumList(composeTestRule)
        albumList.getAddAlbumButton().assertIsDisplayed().performClick()

        val albumCreate = AlbumCreate(composeTestRule)

        // And I write a name
        val name = "Name: " + java.util.UUID.randomUUID().toString()
        albumCreate.fillBasicInput(name, "create-name")

        // And I write a cover url
        val cover = "https://" + java.util.UUID.randomUUID().toString() + ".jpg"
        albumCreate.fillBasicInput(cover, "create-cover")

        // And I select a genre
        albumCreate.selectOption("create-genre", 0)

        // And I select a recordLabel
        albumCreate.selectOption("create-recordLabel", 0)

        // And I write a description
        val description = "Description: " + java.util.UUID.randomUUID().toString()
        albumCreate.fillBasicInput(description, "create-description")

        // And I click the submit button
        albumCreate.getSubmitButton().assertIsDisplayed().performClick()

        // Then I see a message that the comment has been added successfully
        albumCreate.findExactlyOne(
            hasText("Álbum agregado exitosamente")
        ).assertIsDisplayed()

    }
}
