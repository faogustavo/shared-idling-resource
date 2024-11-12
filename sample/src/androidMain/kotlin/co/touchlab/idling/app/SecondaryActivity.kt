package co.touchlab.idling.app

import MyViewModel
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SecondaryActivity : ComponentActivity(R.layout.secondary_activity) {
    private val vm by viewModels<MyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpUI()
        registerListeners()
    }

    private fun setUpUI() {
        findViewById<TextView>(R.id.increment).setOnClickListener {
            vm.increment()
        }
        findViewById<TextView>(R.id.decrement).setOnClickListener {
            vm.decrement()
        }
    }

    private fun registerListeners() {
        val presenter = findViewById<TextView>(R.id.presenter)

        lifecycleScope.launch {
            vm.myState.collect { state ->
                presenter.text = state.toString()
            }
        }
    }
}
