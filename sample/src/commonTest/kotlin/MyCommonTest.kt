import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class MyCommonTest {
    @Test
    fun myTest_idling() =
        runComposeUiTest {
            registerIdlingResource(MyCustomCommonIdlingResource)

            setContent { MyScreen() }

            onNodeWithTag("ValuePresenter")
                .assertExists()
                .assertTextEquals("0")

            onNodeWithTag("IncrementButton")
                .assertExists()
                .performClick()

            onNodeWithTag("ValuePresenter")
                .assertExists()
                .assertTextEquals("1")

            onNodeWithTag("DecrementButton")
                .assertExists()
                .performClick()

            onNodeWithTag("ValuePresenter")
                .assertExists()
                .assertTextEquals("0")

            unregisterIdlingResource(MyCustomCommonIdlingResource)
        }

    @Test
    fun myTest_composeTestOnly() =
        runComposeUiTest {
            setContent { MyScreen() }

            onNodeWithTag("ValuePresenter")
                .assertExists()
                .assertTextEquals("0")

            onNodeWithTag("IncrementButton")
                .assertExists()
                .performClick()

//            onNodeWithTag("ValuePresenter")
//                .assertExists()
//                .assertTextEquals("1")

            waitUntilAtLeastOneExists(hasTestTag("ValuePresenter") and hasText("1"))

            onNodeWithTag("DecrementButton")
                .assertExists()
                .performClick()

//            onNodeWithTag("ValuePresenter")
//                .assertExists()
//                .assertTextEquals("0")

            waitUntilAtLeastOneExists(hasTestTag("ValuePresenter") and hasText("0"))
        }
}
