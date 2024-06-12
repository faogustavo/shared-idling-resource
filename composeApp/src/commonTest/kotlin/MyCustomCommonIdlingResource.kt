import androidx.compose.ui.test.IdlingResource
import co.touchlab.idling.ComposeIdlingResource

object MyCustomCommonIdlingResource : IdlingResource by ComposeIdlingResource(MyCustomIdlingResource)