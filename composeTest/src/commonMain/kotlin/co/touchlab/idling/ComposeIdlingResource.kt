package co.touchlab.idling

import androidx.compose.ui.test.IdlingResource
import co.touchlab.idling.IdlingResource as KMPIdlingResource

class ComposeIdlingResource(private val delegate: KMPIdlingResource) : IdlingResource {
    override val isIdleNow: Boolean get() = delegate.isIdleNow
    override fun getDiagnosticMessageIfBusy(): String? = delegate.idleMessageIfBusy
}