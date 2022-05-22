package app.revanced.utils.signature

import app.revanced.patcher.Patcher
import app.revanced.patcher.extensions.findAnnotationRecursively
import app.revanced.patcher.signature.implementation.method.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.signature.implementation.method.annotation.MatchingMethod
import org.jf.dexlib2.iface.Method

object Signature {

    fun checkSignatures(patcher: Patcher) {
        val failed = mutableListOf<String>()
        for (signature in patcher.resolveSignatures()) {
            val signatureClass = signature::class.java
            val signatureName =
                signatureClass.findAnnotationRecursively(app.revanced.patcher.annotation.Name::class.java)?.name
                    ?: signatureClass.name
            if (!signature.resolved) {
                failed.add(signatureName)
                continue
            }

            val method = signature.result!!.method
            val matchingMethod =
                signatureClass.findAnnotationRecursively(MatchingMethod::class.java) ?: MatchingMethod()

            println(
                """
                    [Signature] $signatureName
                    [Method] ${matchingMethod.definingClass}->${matchingMethod.name}
                    [Match] ${method.definingClass}->${method.toStr()}
                """.trimIndent()
            )

            signatureClass.findAnnotationRecursively(FuzzyPatternScanMethod::class.java)?.let {
                val warnings = signature.result!!.scanResult.warnings!!
                println(
                    """
                        [Warnings: ${warnings.count()}]
                        ${warnings.joinToString(separator = "\n") { warning -> "${warning.instructionIndex} / ${warning.patternIndex}: ${warning.wrongOpcode} (expected: ${warning.correctOpcode})" }}
                    """.trimIndent()
                )
            }
        }

        println(
            """
                ${"=".repeat(50)}
                [Failed signatures: ${failed.size}]
                ${failed.joinToString(separator = "\n") { it }}
        """.trimIndent()
        )
    }

    private fun Method.toStr(): String {
        return "${this.name}(${this.parameterTypes.joinToString("")})${this.returnType}"
    }
}