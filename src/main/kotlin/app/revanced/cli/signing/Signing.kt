package app.revanced.cli.signing

import app.revanced.cli.command.MainCommand.args
import app.revanced.cli.command.MainCommand.logger
import app.revanced.utils.signing.Signer
import app.revanced.utils.signing.align.ZipAligner
import java.io.File

object Signing {
    fun start(inputFile: File, outputFile: File, cn: String, password: String) {
        val cacheDirectory = File(args.pArgs!!.cacheDirectory)
        val alignedOutput = cacheDirectory.resolve("${outputFile.nameWithoutExtension}_aligned.apk")
        val signedOutput = cacheDirectory.resolve("${outputFile.nameWithoutExtension}_signed.apk")

        logger.info("Align zip entries")

        // align the inputFile and write to alignedOutput
        ZipAligner.align(inputFile, alignedOutput)

        logger.info("Sign zip file")

        // sign the alignedOutput and write to signedOutput
        // the reason is, in case the signer fails
        // it does not damage the output file
        val keyStore = Signer(cn, password).signApk(alignedOutput, signedOutput)

        // afterwards copy over the file and the keystore to the output
        signedOutput.copyTo(outputFile, true)
        keyStore.copyTo(outputFile.resolveSibling(keyStore.name), true)
    }
}
