package io.viewpoint.dagger.assisted.factory.processor

import com.squareup.kotlinpoet.ClassName

object TypeNames {
    val VIEW_MODEL = ClassName("androidx.lifecycle", "ViewModel")

    val VIEW_MODEL_FACTORY = ClassName("androidx.lifecycle", "ViewModelProvider", "Factory")
}