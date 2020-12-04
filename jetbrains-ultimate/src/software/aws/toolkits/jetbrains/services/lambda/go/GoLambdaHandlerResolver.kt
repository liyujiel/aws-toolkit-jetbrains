// Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.services.lambda.go

import com.goide.stubs.index.GoFunctionIndex
import com.goide.stubs.index.GoIdFilter
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import software.aws.toolkits.jetbrains.services.lambda.LambdaHandlerResolver

class GoLambdaHandlerResolver : LambdaHandlerResolver {
    override fun version(): Int = 1

    override fun findPsiElements(project: Project, handler: String, searchScope: GlobalSearchScope): Array<NavigatablePsiElement> =
        // GoFunctionDeclarationImpl is a NavigatablePsiElement
        GoFunctionIndex.find(handler, project, searchScope, GoIdFilter.getFilesFilter(searchScope)).filterIsInstance<NavigatablePsiElement>().toTypedArray()

    override fun determineHandler(element: PsiElement): String? {
        // TODO
        //if (!element.isValidHandlerIdentifier()) {
        //    return null
        //}

        val virtualFile = element.containingFile.virtualFile ?: return null

      //  val sourceRoot = inferSourceRoot(element.project, virtualFile) ?: return null
      //  val relativePath = VfsUtilCore.findRelativePath(sourceRoot, virtualFile, '/') ?: return null
        //val prefix = FileUtilRt.getNameWithoutExtension(relativePath)
        val handlerName = element.text

        return handlerName
        //return "$prefix.$handlerName"
    }

    override fun determineHandlers(element: PsiElement, file: VirtualFile): Set<String> = determineHandler(element)?.let { setOf(it) }.orEmpty()
}
