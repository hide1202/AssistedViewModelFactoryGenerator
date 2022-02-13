package io.viewpoint.dagger.assisted

import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ExampleRepository @Inject constructor() {
    var value: Long = 0L
}