package app.revanced.cli.aligning

import app.revanced.cli.command.MainCommand
import app.revanced.cli.command.MainCommand.logger
import app.revanced.utils.signing.align.ZipAligner
import java.io.File

object Aligning {
    fun align(inputFile: File, outputFile: File) {
        val cacheDirectory = File(MainCommand.args.sArgs?.pArgs?.cacheDirectory)
        val alignedOutput = cacheDirectory.resolve("${outputFile.nameWithoutExtension}_aligned.apk")

        logger.info("Aligning ${inputFile.name}")
        ZipAligner.align(inputFile, alignedOutput)

        logger.info("Copying ${alignedOutput.name} to ${outputFile.name}")
        alignedOutput.copyTo(outputFile, true)
        }
    }
