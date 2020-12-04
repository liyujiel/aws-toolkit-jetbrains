// Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.services.lambda.go

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import software.aws.toolkits.jetbrains.services.lambda.LambdaBuilder
import software.aws.toolkits.jetbrains.services.lambda.nodejs.inferSourceRoot
import java.nio.file.Path
import java.nio.file.Paths

class GoLambdaBuilder : LambdaBuilder() {
    // TODO write
    override fun handlerBaseDirectory(module: Module, handlerElement: PsiElement): Path {
        val handlerVirtualFile = ReadAction.compute<VirtualFile, Throwable> {
            handlerElement.containingFile?.virtualFile
                ?: throw IllegalArgumentException("Handler file must be backed by a VirtualFile")
        }
        return Paths.get(ProjectFileIndex.getInstance(module.project).getContentRootForFile(handlerVirtualFile)!!.path)
    }
}
