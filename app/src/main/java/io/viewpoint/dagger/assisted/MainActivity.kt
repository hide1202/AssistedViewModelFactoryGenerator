package io.viewpoint.dagger.assisted

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    internal lateinit var factories: AssistedViewModelFactories

    private val viewModel: AssistedViewModel by viewModels {
        factories.createassistedViewModel("1", listOf("a", "b", "c"))
    }

    private val viewModel2: AssistedViewModel2 by viewModels {
        factories.createassistedViewModel2(7L)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "viewModel.exampleId: ${viewModel.exampleId}")
        Log.d(TAG, "viewModel.names: ${viewModel.names}")
        Log.d(TAG, "viewModel2.id: ${viewModel2.id}")
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}