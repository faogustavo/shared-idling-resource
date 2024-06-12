import androidx.compose.ui.test.*
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class MyCommonTest {

    @Test
    fun myTest() = runComposeUiTest {
        registerIdlingResource(MyCustomCommonIdlingResource)
        waitForIdle()

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
}