package io.viewpoint.dagger.assisted.factory.processor

import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSDefaultVisitor

class ViewModelVisitor : KSDefaultVisitor<Unit, AssistedViewModelFactoryGenerator?>() {
    override fun defaultHandler(node: KSNode, data: Unit): AssistedViewModelFactoryGenerator? {
        val targetConstructor = node as? KSFunctionDeclaration ?: return null
        val viewModelClass = node.parent as? KSClassDeclaration ?: return null

        val isConstructor = targetConstructor.isConstructor()
        val isViewModel = viewModelClass.superTypes
            .any {
                val typeDeclaration = it.resolve().declaration

                TypeNames.VIEW_MODEL.canonicalName == typeDeclaration.qualifiedName?.asString()
            }
        if (!isConstructor || !isViewModel) {
            return null
        }

        return AssistedViewModelFactoryGenerator(viewModelClass, targetConstructor)
    }
}