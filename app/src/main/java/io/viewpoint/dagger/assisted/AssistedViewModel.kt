package io.viewpoint.dagger.assisted

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class AssistedViewModel @AssistedInject constructor(
    repository: ExampleRepository,
    @Assisted val exampleId: String,
    @Assisted val names: List<String>
) : ViewModel() {
}