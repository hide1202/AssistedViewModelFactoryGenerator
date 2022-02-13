package io.viewpoint.dagger.assisted.factory.processor

import com.squareup.kotlinpoet.ClassName

object TypeNames {
    val VIEW_MODEL = ClassName("androidx.lifecycle", "ViewModel")

    val HILT_VIEW_MODEL = ClassName("dagger.hilt.android.lifecycle", "HiltViewModel")

    val VIEW_MODEL_FACTORY = ClassName("androidx.lifecycle", "ViewModelProvider", "Factory")
}