package app.revanced.utils.signature

import app.revanced.patcher.Patcher

object Signature {

    fun checkSignatures(patcher: Patcher) {
        TODO()
        /**
        val failed = mutableListOf<String>()
        for (signature in patcher.resolveSignatures()) {
        val signatureClass = signature::class.java
        val signatureName = signature.name ?: signatureClass.simpleName
        if (!signature.resolved) {
        failed.add(signatureName)
        continue
        }

        val method = signature.result!!.method
        val matchingMethod = signature.matchingMethod ?: MatchingMethod()

        println(
        """
        [Signature] $signatureName
        [Method] ${matchingMethod.definingClass}->${matchingMethod.name}
        [Match] ${method.definingClass}->${method.toStr()}
        """.trimIndent()
        )

        signature.fuzzyThreshold.let {
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
         */
    }

    //private fun Method.toStr(): String {
    //    return "${this.name}(${this.parameterTypes.joinToString("")})${this.returnType}"
    //}
}