package io.viewpoint.dagger.assisted.factory.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.writeTo
import javax.inject.Inject
import javax.inject.Singleton

class AssistedViewModelFactoryProcessor(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val logger: KSPLogger = environment.logger

    @OptIn(KspExperimental::class, KotlinPoetKspPreview::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.warn("start processor!")

        val symbols = resolver.getSymbolsWithAnnotation(ASSISTED_INJECT_CLASS_NAME)
        if (symbols.none()) {
            return emptyList()
        }

        val generatorList = mutableListOf<AssistedViewModelFactoryGenerator>()
        for (type in resolver.getSymbolsWithAnnotation(ASSISTED_INJECT_CLASS_NAME)) {
            val targetConstructor = type as? KSFunctionDeclaration ?: continue
            val viewModelClass = type.parent as? KSClassDeclaration ?: continue

            val isConstructor = targetConstructor.isConstructor()
            val isViewModel = viewModelClass.superTypes
                .any {
                    val typeDeclaration = it.resolve().declaration
                    val packageName = typeDeclaration.packageName.asString()
                    val typeName = typeDeclaration.simpleName.asString()

                    packageName == "androidx.lifecycle" && typeName == "ViewModel"
                }
            if (!isConstructor || !isViewModel) {
                continue
            }

            generatorList.add(AssistedViewModelFactoryGenerator(viewModelClass, targetConstructor))
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

        val fileSpec =
            FileSpec.builder(packageName, "Factories")
                .apply {
                    generatorList
                        .map { it.createFactoryTypeSpec() }
                        .forEach {
                            addType(it)
                        }
                }
                .addImport("androidx.lifecycle", "ViewModel")
                .addType(TypeSpec.classBuilder("Factories")
                    .addAnnotation(Singleton::class)
                    .primaryConstructor(FunSpec.constructorBuilder()
                        .addAnnotation(Inject::class)
                        .apply {
                            factoryProperties.forEach {
                                addParameter(it.first, it.second)
                            }
                        }
                        .build()
                    )
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
        fileSpec.writeTo(codeGenerator, Dependencies.ALL_FILES)

        return emptyList()
    }

    companion object {
        private const val ASSISTED_CLASS_NAME = "dagger.assisted.Assisted"
        private const val ASSISTED_INJECT_CLASS_NAME = "dagger.assisted.AssistedInject"
    }
}

data class ParameterInfo(
    val name: String,
    val type: TypeName
)