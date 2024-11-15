import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import co.touchlab.idling.app.R
import co.touchlab.idling.app.SecondaryActivity
import idling.MyCustomEspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class EspressoIdlingResourceTest {
    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(MyCustomEspressoIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(MyCustomEspressoIdlingResource)
    }

    @Test
    fun myTest() {
        launchActivity<SecondaryActivity>().use {
            onView(withId(R.id.presenter)).check(matches(withText("0")))

            onView(withId(R.id.increment)).perform(click())
            onView(withId(R.id.presenter)).check(matches(withText("1")))

            onView(withId(R.id.decrement)).perform(click())
            onView(withId(R.id.presenter)).check(matches(withText("0")))
        }
    }

    @Test
    fun myTest_compose() =
        runComposeUiTest {
            setContent { MyScreen() }

            onNodeWithTag("ValuePresenter").assertExists().assertTextEquals("0")

            onNodeWithTag("IncrementButton").assertExists().performClick()
            onNodeWithTag("ValuePresenter").assertExists().assertTextEquals("1")

            onNodeWithTag("DecrementButton").assertExists().performClick()
            onNodeWithTag("ValuePresenter").assertExists().assertTextEquals("0")
        }
}
