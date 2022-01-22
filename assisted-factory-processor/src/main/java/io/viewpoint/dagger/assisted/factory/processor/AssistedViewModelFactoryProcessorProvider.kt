package io.viewpoint.dagger.assisted.factory.processor

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

@AutoService(SymbolProcessorProvider::class)
class AssistedViewModelFactoryProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        AssistedViewModelFactoryProcessor(environment)
}