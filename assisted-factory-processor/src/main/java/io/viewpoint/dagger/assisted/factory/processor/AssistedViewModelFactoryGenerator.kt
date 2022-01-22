package io.viewpoint.dagger.assisted.factory.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

@OptIn(KspExperimental::class, KotlinPoetKspPreview::class)
class AssistedViewModelFactoryGenerator(
    private val viewModelClass: KSClassDeclaration,
    private val primaryConstructor: KSFunctionDeclaration
) {
    private val viewModelClassName: TypeName = viewModelClass.asType(emptyList()).toClassName()

    private val assistedConstructorParameters = primaryConstructor.parameters
        .filter {
            it.isAnnotationPresent(Assisted::class)
        }
        .map { param ->
            ParameterInfo(requireNotNull(param.name?.asString()), param.type.toTypeName())
        }

    fun createFactoryTypeSpec(): TypeSpec {
        return TypeSpec.interfaceBuilder("${viewModelClass}Factory")
            .addFunction(
                FunSpec.builder("create")
                    .addModifiers(KModifier.ABSTRACT)
                    .apply {
                        assistedConstructorParameters.forEach {
                            addParameter(it.name, it.type)
                        }
                    }
                    .returns(viewModelClassName)
                    .build()
            )
            .addAnnotation(AssistedFactory::class)
            .build()
    }

    fun createFactoryFunSpec(): FunSpec {
        val createFunctionName = "create$camelViewModelClassName"
        val parametersString = assistedConstructorParameters.joinToString(
            ", "
        ) { it.name }
        return FunSpec.builder(createFunctionName)
            .apply {
                assistedConstructorParameters.forEach {
                    addParameter(it.name, it.type)
                }
            }
            .addCode(
                """
                    return object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                            return ${camelViewModelClassName}Factory.create(${parametersString}) as T
                        }
                    }
                """.trimIndent()
            )
            .returns(
                ClassName(
                    "androidx.lifecycle",
                    listOf("ViewModelProvider", "Factory")
                )
            )
            .build()
    }

    private val camelViewModelClassName: String =
        viewModelClass.toString().replaceFirstChar { it.lowercase() }
}
