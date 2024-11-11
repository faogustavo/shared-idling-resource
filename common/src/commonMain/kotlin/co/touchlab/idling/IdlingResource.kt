package co.touchlab.idling

interface IdlingResource {
    val name: String
    val isIdleNow: Boolean
    val idleMessageIfBusy: String?

    fun registerIdleCallback(callback: IdlingCallback)
}
