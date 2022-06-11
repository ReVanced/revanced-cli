package app.revanced.cli.signing

import app.revanced.cli.command.MainCommand.cacheDirectory
import app.revanced.utils.signing.Signer
import app.revanced.utils.signing.align.ZipAligner
import java.io.File

object Signing {
    fun start(inputFile: File, outputFile: File, cn: String, password: String) {
        // align & sign
        val cacheDirectory = File(cacheDirectory)
        val alignedOutput = cacheDirectory.resolve("aligned.apk")
        val signedOutput = cacheDirectory.resolve("signed.apk")
        ZipAligner.align(inputFile, alignedOutput)
        Signer(
            cn,
            password
        ).signApk(inputFile, signedOutput)

        // write to output
        signedOutput.copyTo(outputFile)
    }
}
