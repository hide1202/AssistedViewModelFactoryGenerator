package io.viewpoint.dagger.assisted

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class AssistedViewModel @AssistedInject constructor(
    private val repository: ExampleRepository,
    @Assisted val exampleId: String,
    @Assisted val names: List<String>
) : ViewModel() {
    init {
        // check whether instance is same or not when the property has ViewModelScoped
        repository.value++
    }

    val value: Long
        get() = repository.value
}