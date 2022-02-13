package io.viewpoint.dagger.assisted

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val factories: AssistedViewModelFactoryViewModel by viewModels()

    private val viewModel: AssistedViewModel by viewModels {
        factories.createAssistedViewModel("1", listOf("a", "b", "c"))
    }

    private val viewModel2: AssistedViewModel2 by viewModels {
        factories.createAssistedViewModel2(7L)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "viewModel.exampleId: ${viewModel.exampleId}")
        Log.d(TAG, "viewModel.names: ${viewModel.names}")
        Log.d(TAG, "viewModel2.id: ${viewModel2.id}")
        Log.d(TAG, "viewModel.value: ${viewModel.value}")
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}