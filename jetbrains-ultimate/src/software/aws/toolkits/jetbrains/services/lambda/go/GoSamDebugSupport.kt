// Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.services.lambda.go

import com.goide.dlv.DlvDebugProcess
import com.goide.dlv.DlvDisconnectOption
import com.goide.dlv.DlvRemoteVmConnection
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

            val connection = DlvRemoteVmConnection(DlvDisconnectOption.KILL)
            connection.address = InetSocketAddress(debugHost, debugPorts.first())

            return DlvDebugProcess(
                session,
                connection,
                executionResult,
                true
            )
        }
    }

    override fun samArguments(runtime: Runtime, packageType: PackageType, debugPorts: List<Int>): List<String> = buildList {
        add("--debugger-path")
        // TODO fix
        add(localDlv().parentFile.parentFile.resolve("linux").absolutePath)
        add("--debug-args")
        if (packageType == PackageType.IMAGE) {
            add(
                "/var/runtime/aws-lambda-go delveAPI -delveAPI=2 -delvePort=${debugPorts.first()} -delvePath=/tmp/lambci_debug_files/dlv"
            )
        } else {
            add(""""-delveAPI=2"""")
        }
    }
}
