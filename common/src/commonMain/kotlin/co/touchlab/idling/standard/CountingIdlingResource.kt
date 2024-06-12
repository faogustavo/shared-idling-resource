package co.touchlab.idling.standard

import co.touchlab.idling.IdlingCallback
import co.touchlab.idling.IdlingResource
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update

open class CountingIdlingResource(override val name: String) : IdlingResource {
    companion object {
        val Shared = CountingIdlingResource("SharedCountingIdlingResource")
    }

    private val count = atomic(0)
    private val callback = atomic<IdlingCallback?>(null)

    override val isIdleNow: Boolean
        get() = count.value == 0

    override val idleMessageIfBusy: String?
        get() = "Expected count to be '0' but was '${count.value}'".takeIf { isIdleNow }

    fun increment() {
        count.incrementAndGet()
    }

    fun decrement() {
        if (count.decrementAndGet() == 0) {
            this.callback.value?.onTransitionToIdle()
        }
    }

    override fun registerIdleCallback(callback: IdlingCallback) {
        this.callback.update { callback }
    }
}