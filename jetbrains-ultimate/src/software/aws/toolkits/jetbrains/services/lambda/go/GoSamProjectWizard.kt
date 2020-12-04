// Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.services.lambda.go

import com.goide.sdk.combobox.GoSdkChooserCombo
import com.intellij.facet.ui.ValidationResult
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import software.amazon.awssdk.services.lambda.model.PackageType
import software.amazon.awssdk.services.lambda.model.Runtime
import software.aws.toolkits.jetbrains.services.lambda.wizard.SamAppTemplateBased
import software.aws.toolkits.jetbrains.services.lambda.wizard.SamProjectTemplate
import software.aws.toolkits.jetbrains.services.lambda.wizard.SamProjectWizard
import software.aws.toolkits.jetbrains.services.lambda.wizard.SdkSelector
import software.aws.toolkits.jetbrains.utils.ui.validationInfo
import software.aws.toolkits.resources.message
import javax.swing.JComponent
import javax.swing.JLabel

class GoSamProjectWizard : SamProjectWizard {
    override fun createSdkSelectionPanel(projectLocation: TextFieldWithBrowseButton?): SdkSelector = GoSdkSelectionPanel()

    override fun listTemplates(): Collection<SamProjectTemplate> = listOf(
        SamHelloWorldGo()
    )
}

// TODO add source roots on create
class GoSdkSelectionPanel : SdkSelector {
    private val interpreterPanel = GoSdkChooserCombo()

    override fun sdkSelectionLabel() = JLabel(message("sam.init.go.sdk"))

    override fun sdkSelectionPanel(): JComponent = interpreterPanel

    override fun validateSelection(): ValidationInfo? = interpreterPanel.validator.validate(interpreterPanel.sdk)?.let {
        if (it == ValidationResult.OK) {
            return null
        }
        interpreterPanel.validationInfo(it.errorMessage)
    }
}

class SamHelloWorldGo : SamAppTemplateBased() {
    override fun displayName() = message("sam.init.template.hello_world.name")

    override fun description() = message("sam.init.template.hello_world.description")

    override fun supportedRuntimes(): Set<Runtime> = setOf(Runtime.GO1_X)

    override fun supportedPackagingTypes(): Set<PackageType> = setOf(PackageType.IMAGE, PackageType.ZIP)

    override val appTemplateName: String = "hello-world"

    override val dependencyManager: String = "mod"
}
