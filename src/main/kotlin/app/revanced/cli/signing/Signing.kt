package app.revanced.cli.signing

import app.revanced.cli.command.MainCommand.args
import app.revanced.cli.command.MainCommand.logger
import app.revanced.utils.signing.Signer
import app.revanced.utils.signing.align.ZipAligner
import java.io.File

object Signing {
    fun start(patchedFile: File, outputFile: File, signingOptions: SigningOptions) {
        val cacheDirectory = File(args.sArgs?.pArgs?.cacheDirectory)
        val signedOutput = cacheDirectory.resolve("${outputFile.nameWithoutExtension}_signed.apk")
        val alignedOutput = cacheDirectory.resolve("${outputFile.nameWithoutExtension}_aligned.apk")

        // align the patchedFile and write to alignedFile
        ZipAligner.align(patchedFile, alignedOutput)

        // sign the alignedOutput and write to signedOutput
        // the reason is, in case the signer fails
        // it does not damage the output file
        logger.info("Signing ${alignedOutput.name}")
        Signer(signingOptions).signApk(alignedOutput, signedOutput)

        // afterwards copy over the file to the output
        logger.info("Copying ${signedOutput.name} to ${outputFile.name}")
        signedOutput.copyTo(outputFile, true)
    }
}
