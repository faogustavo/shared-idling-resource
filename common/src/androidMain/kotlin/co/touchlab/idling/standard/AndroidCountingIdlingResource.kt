package co.touchlab.idling.standard

import androidx.test.espresso.IdlingResource
import co.touchlab.idling.AndroidIdlingResource

class AndroidCountingIdlingResource(
    countingIdlingResource: CountingIdlingResource,
) : IdlingResource by AndroidIdlingResource(countingIdlingResource) {
    companion object {
        val Shared = AndroidCountingIdlingResource(CountingIdlingResource.Shared)
    }
}