package app.revanced.cli.aligning

import app.revanced.cli.command.MainCommand.logger
import app.revanced.utils.signing.align.ZipAligner
import java.io.File

object Aligning {
    fun align(inputFile: File, outputFile: File) {
        logger.info("Aligning ${inputFile.name} to ${outputFile.name}")
        ZipAligner.align(inputFile, outputFile)
    }
}
