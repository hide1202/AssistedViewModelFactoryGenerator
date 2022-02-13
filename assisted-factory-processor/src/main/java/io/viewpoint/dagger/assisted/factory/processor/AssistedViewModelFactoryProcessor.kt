package io.viewpoint.dagger.assisted.factory.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.writeTo
import dagger.assisted.AssistedInject
import javax.inject.Inject

class AssistedViewModelFactoryProcessor(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val logger: KSPLogger = environment.logger

    @OptIn(KspExperimental::class, KotlinPoetKspPreview::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.warn("start processor!")

        val viewModelVisitor = ViewModelVisitor()
        val generatorList = resolver
            .getSymbolsWithAnnotation(requireNotNull(AssistedInject::class.qualifiedName))
            .mapNotNull {
                it.accept(viewModelVisitor, Unit)
            }
            .toList()
        if (generatorList.none()) {
            return emptyList()
        }

        // TODO 패키지 변경
        val packageName = "io.viewpoint.dagger.assisted"

        val factoryProperties = mutableListOf<Pair<String, ClassName>>()
        generatorList
            .map {
                it.createFactoryTypeSpec()
            }
            .forEach { typeSpec ->
                val typeName = typeSpec.name ?: return@forEach
                val paramName = typeName
                    .replaceFirstChar { it.lowercaseChar() }
                factoryProperties.add(paramName to ClassName(packageName, typeName))
            }

        val factoriesClassName = "AssistedViewModelFactoryViewModel"
        val fileSpec =
            FileSpec.builder(packageName, factoriesClassName)
                .apply {
                    generatorList
                        .map { it.createFactoryTypeSpec() }
                        .forEach {
                            addType(it)
                        }
                }
                .addType(TypeSpec.classBuilder(factoriesClassName)
                    .addAnnotation(TypeNames.HILT_VIEW_MODEL)
                    .primaryConstructor(FunSpec.constructorBuilder()
                        .addAnnotation(Inject::class)
                        .apply {
                            factoryProperties.forEach {
                                addParameter(it.first, it.second)
                            }
                        }
                        .build()
                    )
                    .superclass(TypeNames.VIEW_MODEL)
                    .apply {
                        factoryProperties.forEach { (name, className) ->
                            addProperty(
                                PropertySpec.builder(name, className)
                                    .initializer(name)
                                    .addModifiers(KModifier.PRIVATE)
                                    .build()
                            )
                        }
                        generatorList
                            .map { it.createFactoryFunSpec() }
                            .forEach {
                                addFunction(it)
                            }
                    }
                    .build())
                .build()

        val codeGenerator = environment.codeGenerator
        val dependenciesFiles = generatorList
            .mapNotNull {
                it.containingFile
            }
        fileSpec.writeTo(codeGenerator, Dependencies(true, *dependenciesFiles.toTypedArray()))

        return emptyList()
    }
}

data class ParameterInfo(
    val name: String,
    val type: TypeName
)