package shaketaunt.mobile.mypapit.net.shaketaunt

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShakeTauntInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ShakeMe>()

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("shaketaunt.mobile.mypapit.net.shaketaunt", appContext.packageName)
    }

    @Test
    fun startupPromptShownAndShakeButtonGeneratesTaunt() {
        composeTestRule.onNodeWithTag("taunt_text")
            .assertIsDisplayed()
            .assertTextContains("Just Shake Me!")

        composeTestRule.onNodeWithTag("shake_button")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.onNodeWithTag("taunt_text")
            .assertIsDisplayed()
            .assertTextContains("You are ", substring = true)
    }

    @Test
    fun aboutDialogOpensAndCloses() {
        composeTestRule.onNodeWithTag("about_button")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.onNodeWithText("About Shake Me!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Close").performClick()
        composeTestRule.onNodeWithText("About Shake Me!").assertDoesNotExist()
    }
}
