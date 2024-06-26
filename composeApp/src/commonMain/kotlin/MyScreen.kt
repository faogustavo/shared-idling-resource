import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import co.touchlab.idling.IdlingCallback
import co.touchlab.idling.IdlingResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Composable
fun MyScreen() {
    val lifecycleOwner = remember { MyLifecycleOwner() }
    val viewModelOwner = remember { MyViewModelStoreOwner() }
    CompositionLocalProvider(
        LocalLifecycleOwner provides lifecycleOwner,
        LocalViewModelStoreOwner provides viewModelOwner,
    ) {
        val viewModel = viewModel { MyViewModel() }

        val state by viewModel.myState.collectAsState()

        Column {
            Text(
                state.toString(),
                modifier = Modifier.testTag("ValuePresenter")
            )

            Button(
                onClick = { viewModel.increment() },
                modifier = Modifier.testTag("IncrementButton"),
            ) {
                Text("Increment")
            }

            Button(
                onClick = { viewModel.decrement() },
                modifier = Modifier.testTag("DecrementButton"),
            ) {
                Text("Decrement")
            }
        }
    }
}

class MyViewModel : ViewModel() {
    val myState = MutableStateFlow(0)

    fun increment() = update { it + 1 }
    fun decrement() = update { it - 1 }

    private fun update(transform: (Int) -> Int) {
        viewModelScope.launch {
            MyCustomIdlingResource.lock()
            delay(500)
            myState.update(transform)
        }.invokeOnCompletion { MyCustomIdlingResource.release() }
    }
}

class MyLifecycleOwner : LifecycleOwner {
    override val lifecycle: Lifecycle = LifecycleRegistry(this)
}

class MyViewModelStoreOwner : ViewModelStoreOwner {
    override val viewModelStore = ViewModelStore()
}

object MyCustomIdlingResource : IdlingResource {
    private var isLocked = false
    private var callback: IdlingCallback? = null

    override val name: String = "Custom Lock Resource"
    override val isIdleNow: Boolean get() = !isLocked
    override val idleMessageIfBusy: String? get() = "Resource is locked".takeIf { isLocked }

    fun lock() {
        isLocked = true
    }

    fun release() {
        isLocked = false
        callback?.onTransitionToIdle()
    }

    override fun registerIdleCallback(callback: IdlingCallback) {
        this.callback = callback
    }
}