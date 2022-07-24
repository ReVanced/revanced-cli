package app.revanced.cli.signing

import app.revanced.cli.command.MainCommand.logger
import app.revanced.utils.signing.Signer
import java.io.File

object Signing {
    fun sign(alignedFile: File, signedOutput: File, signingOptions: SigningOptions) {
        logger.info("Signing ${alignedFile.name} to ${signedOutput.name}")
        Signer(signingOptions).signApk(alignedFile, signedOutput)
    }
}
