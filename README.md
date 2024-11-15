# Shared Idling Resource

Utility library for using [Idling Resources](https://developer.android.com/training/testing/espresso/idling-resource) on
both Android and Compose Multiplatform.

## Installation

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            // To implement Idling Resources in common code
            implementation("co.touchlab.idling:common:${Version}")
        }

        commonTest.dependencies {
            // For Compose Multiplatform Support
            implementation("co.touchlab.idling:composeTest:${Version}")
        }

        androidInstrumentedTest.dependencies {
            // For Android Espresso Support
            implementation("co.touchlab.idling:espressoTest:${Version}")
        }
    }
}
```

## Creating an Idle Resource

On your common code, you will now have access to the `co.touchlab.idling.IdlingResource` class now. You can use this
class in a similar you would use the Android version.

To create a new Idling Resource, you can just create a new class/object inheriting from this interface.

```kotlin
object MyCustomIdlingResource : IdlingResource {
    // Store if this item is idle or not
    private var isLocked = false

    // Callback used to move to idle
    private var callback: IdlingCallback? = null

    // Class properties that you must implement

    // Resource name required for Android
    override val name: String = "Custom Lock Resource"

    // Returns true if this resource is idle now
    // Remember to make it a getter
    override val isIdleNow: Boolean get() = !isLocked

    // Lock message required for JVM
    override val idleMessageIfBusy: String? get() = "Resource is locked".takeIf { isLocked }

    // This callback is called when it's attached to the tests
    override fun registerIdleCallback(callback: IdlingCallback) {
        this.callback = callback
    }

    // Methods to lock/release this resource
    fun lock() {
        isLocked = true
    }

    fun release() {
        isLocked = false
        callback?.onTransitionToIdle()
    }
}
```

Then, when you need to do some heavy work, you can simply call `increment` and `decrement` to wait the time to finish:

```kotlin
class MyViewModel : ViewModel() {
    val myState = MutableStateFlow(0)

    fun increment() = update { it + 1 }
    fun decrement() = update { it - 1 }

    private fun update(transform: (Int) -> Int) {
        // Lock before start
        MyCustomIdlingResource.lock()

        // Heavy work
        val job = viewModelScope.launch {
            delay(500)
            myState.update(transform)
        }

        // Release on complete
        job.invokeOnCompletion { MyCustomIdlingResource.release() }
    }
}
```

## Using the idle resource

This idle resource can be used in two scenarios:

1. Android Instrumentation/Espresso tests
2. Compose Multiplatform Tests

Each of them have a different usage, but they are very similar:

### Android Espresso Test

To use the common Idling Resource in the Android Tests, you must wrap with an `EspressoIdlingResource`.

The example `MyCustomIdlingResource` above would be wrapped like the following:

```kotlin
import androidx.test.espresso.IdlingResource
import co.touchlab.idling.EspressoIdlingResource

object MyCustomEspressoIdlingResource : IdlingResource by EspressoIdlingResource(MyCustomIdlingResource)
```

Then you can register it as any native Idle Resource:

```kotlin
fun register() {
    IdlingRegistry.getInstance().register(
        MyCustomEspressoIdlingResource,
    )
}

fun unregister() {
    IdlingRegistry.getInstance().unregister(
        MyCustomEspressoIdlingResource
    )
}
```

### Compose Test

Just like the Espresso version, you must wrap the common idling resource into the supported type. For that, you can do
something like this:

```kotlin
import androidx.compose.ui.test.IdlingResource
import co.touchlab.idling.ComposeIdlingResource

object MyCustomCommonIdlingResource : IdlingResource by ComposeIdlingResource(MyCustomIdlingResource)
```

For Compose tests, you can't use a global registry. Each test have its own registry that will be used for that test.
Here is an example from a common test:

```kotlin
@OptIn(ExperimentalTestApi::class)
class MyCommonTest {

    @Test
    fun myTest() = runComposeUiTest {
        // Only available inside `runComposeUiTest`
        registerIdlingResource(MyCustomCommonIdlingResource)

        // TODO: Perform Test

        // Only available inside `runComposeUiTest`
        unregisterIdlingResource(MyCustomCommonIdlingResource)
    }
}
```

If you only have tests for JVM and are using the `runDesktopComposeUiTest`, the same methods will be available for you.
