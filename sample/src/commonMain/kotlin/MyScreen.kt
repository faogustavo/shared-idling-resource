import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import co.touchlab.idling.IdlingCallback
import co.touchlab.idling.IdlingResource
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.getAndUpdate
import kotlinx.atomicfu.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
                modifier = Modifier.testTag("ValuePresenter"),
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
    private val _myState = MutableStateFlow(0)
    val myState = _myState.asStateFlow()

    fun increment() = update { it + 1 }

    fun decrement() = update { it - 1 }

    private fun update(transform: (Int) -> Int) {
        viewModelScope
            .launch {
                MyCustomIdlingResource.lock()
                delay(500)
                _myState.update(transform)
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
    private var isLocked = atomic(false)
    private var callback = atomic<IdlingCallback?>(null)

    override val name: String = "Custom Lock Resource"
    override val isIdleNow: Boolean get() = isLocked.value.not()
    override val idleMessageIfBusy: String? get() = "Resource is locked".takeIf { isIdleNow }

    fun lock() {
        isLocked.update { true }
    }

    fun release() {
        isLocked.update { false }
        callback.getAndUpdate { null }?.onTransitionToIdle()
    }

    override fun registerIdleCallback(callback: IdlingCallback) {
        this.callback.update { callback }
    }
}
