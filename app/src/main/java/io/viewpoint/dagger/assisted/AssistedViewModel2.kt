package io.viewpoint.dagger.assisted

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class AssistedViewModel2 @AssistedInject constructor(
    repository: ExampleRepository,
    @Assisted val id: Long,
) : ViewModel() {
    init {
        repository.value++
    }
}