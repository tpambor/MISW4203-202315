package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.test.swipeUp
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class MonkeyTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val pClick = 0.6
    // pScroll = 0.4

    @Test(timeout=90000)
    fun monkey(): Unit = runBlocking {
        // Do 1 minute of monkey testing
        val result = withTimeoutOrNull(60.seconds) {
            while(true) {
                composeTestRule.waitForIdle()
                val nodesClick = composeTestRule.onAllNodes(hasClickAction())
                val countClick = nodesClick.fetchSemanticsNodes().size
                val nodesScroll = composeTestRule.onAllNodes(hasScrollAction())
                val countScroll = nodesScroll.fetchSemanticsNodes().size

                val p = Random.nextDouble()
                if (p < pClick)
                {
                    if (countClick < 1)
                        continue

                    val idx = Random.nextInt(0, countClick)
                    nodesClick[idx].performClick()
                } else {
                    if (countScroll < 1)
                        continue

                    val idx = Random.nextInt(0, countScroll)
                    nodesScroll[idx].performTouchInput {
                        when (Random.nextInt(0, 4)) {
                            0 -> swipeUp()
                            1 -> swipeDown()
                            2 -> swipeLeft()
                            3 -> swipeRight()
                        }
                    }
                }
                try { composeTestRule.waitUntil(1000) { false } } catch (_: ComposeTimeoutException) { }
                delay(1)
            }
        }
        assert(result == null)
    }
}
