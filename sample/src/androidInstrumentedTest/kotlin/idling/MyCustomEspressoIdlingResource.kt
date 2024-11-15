package idling

import MyCustomIdlingResource
import androidx.test.espresso.IdlingResource
import co.touchlab.idling.espresso.EspressoIdlingResource

object MyCustomEspressoIdlingResource : IdlingResource by EspressoIdlingResource(MyCustomIdlingResource)
