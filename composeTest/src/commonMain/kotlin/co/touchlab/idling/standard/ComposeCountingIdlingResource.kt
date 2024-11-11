package co.touchlab.idling.standard

import androidx.compose.ui.test.IdlingResource
import co.touchlab.idling.ComposeIdlingResource

class ComposeCountingIdlingResource(
    countingIdlingResource: CountingIdlingResource,
) : IdlingResource by ComposeIdlingResource(countingIdlingResource) {
    companion object {
        val Shared = ComposeCountingIdlingResource(CountingIdlingResource.Shared)
    }
}
