package app.revanced.cli.signing

import app.revanced.cli.command.MainCommand.args
import app.revanced.utils.signing.Signer
import app.revanced.utils.signing.align.ZipAligner
import java.io.File

object Signing {
    fun start(inputFile: File, outputFile: File, signingOptions: SigningOptions) {
        val cacheDirectory = File(args.pArgs!!.cacheDirectory)
        val alignedOutput = cacheDirectory.resolve("${outputFile.nameWithoutExtension}_aligned.apk")
        val signedOutput = cacheDirectory.resolve("${outputFile.nameWithoutExtension}_signed.apk")

        // align the inputFile and write to alignedOutput
        println("[aligning]")
        ZipAligner.align(inputFile, alignedOutput)
        // sign the alignedOutput and write to signedOutput
        // the reason is, in case the signer fails
        // it does not damage the output file
        println("[signing]")
        Signer(signingOptions).signApk(alignedOutput, signedOutput)

        // afterwards copy over the file to the output
        signedOutput.copyTo(outputFile, true)
    }
}
