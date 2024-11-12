package idling

import MyCustomIdlingResource
import androidx.test.espresso.IdlingResource
import co.touchlab.idling.espresso.EspressoIdlingResource

object MyCustomAndroidIdlingResource : IdlingResource by EspressoIdlingResource(MyCustomIdlingResource)
