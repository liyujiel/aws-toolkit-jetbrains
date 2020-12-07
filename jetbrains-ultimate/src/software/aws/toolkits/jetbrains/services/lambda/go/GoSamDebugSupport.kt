// Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.services.lambda.go

import com.goide.dlv.DlvDebugProcess
import com.goide.dlv.DlvDisconnectOption
import com.goide.dlv.DlvRemoteVmConnection
import com.goide.execution.GoRunUtil.createDlvDebugProcess
import com.goide.execution.GoRunUtil.localDlv
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugProcessStarter
import com.intellij.xdebugger.XDebugSession
import software.amazon.awssdk.services.lambda.model.PackageType
import software.amazon.awssdk.services.lambda.model.Runtime
import software.aws.toolkits.jetbrains.core.utils.buildList
import software.aws.toolkits.jetbrains.services.lambda.execution.sam.SamDebugSupport
import software.aws.toolkits.jetbrains.services.lambda.execution.sam.SamRunningState
import java.net.InetSocketAddress

class GoSamDebugSupport : SamDebugSupport {
    override fun createDebugProcess(
        environment: ExecutionEnvironment,
        state: SamRunningState,
        debugHost: String,
        debugPorts: List<Int>
    ): XDebugProcessStarter = object : XDebugProcessStarter() {
        override fun start(session: XDebugSession): XDebugProcess {
            val executionResult = state.execute(environment.executor, environment.runner)

            return createDlvDebugProcess(
                session,
                executionResult,
                InetSocketAddress(debugHost, debugPorts.first()),
                true,
                DlvDisconnectOption.DETACH
            )
        }
    }

    override fun samArguments(runtime: Runtime, packageType: PackageType, debugPorts: List<Int>): List<String> = buildList {
        // TODO delve ships with the IDE, but it is not marked executable. The first time the IDE runs it, Delve is set executable
        // At that point. Since we don't know if it's executable or not, set it so the user can execute it.
        localDlv().parentFile.parentFile.resolve("linux").resolve("dlv").setExecutable(true, true)
        add("--debugger-path")
        // TODO fix how we resolve this
        add(localDlv().parentFile.parentFile.resolve("linux").absolutePath)
        add("--debug-args")
        if (packageType == PackageType.IMAGE) {
            add(
                "/var/runtime/aws-lambda-go delveAPI -delveAPI=2 -delvePort=${debugPorts.first()} -delvePath=/tmp/lambci_debug_files/dlv"
            )
        } else {
            add("-delveAPI=2")
        }
    }
}
