package co.touchlab.idling.espresso

import androidx.test.espresso.IdlingResource
import co.touchlab.idling.IdlingResource as KMPIdlingResource

class EspressoIdlingResource(
    private val delegate: KMPIdlingResource,
) : IdlingResource {
    override fun getName(): String = delegate.name

    override fun isIdleNow(): Boolean = delegate.isIdleNow

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        delegate.registerIdleCallback { callback.onTransitionToIdle() }
    }
}
