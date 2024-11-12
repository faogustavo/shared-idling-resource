import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import co.touchlab.idling.app.R
import co.touchlab.idling.app.SecondaryActivity
import idling.MyCustomAndroidIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SecondaryActivityTest {
    private val presenter
        get() = onView(withId(R.id.presenter))

    private val increment
        get() = onView(withId(R.id.increment))

    private val decrement
        get() = onView(withId(R.id.decrement))

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(SecondaryActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(MyCustomAndroidIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(MyCustomAndroidIdlingResource)
    }

    @Test
    fun myTest() {
        presenter.check(matches(withText("0")))

        increment.perform(click())
        presenter.check(matches(withText("1")))

        decrement.perform(click())
        presenter.check(matches(withText("0")))
    }
}
