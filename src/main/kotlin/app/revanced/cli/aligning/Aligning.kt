package app.revanced.cli.aligning

import app.revanced.cli.command.MainCommand.logger
import app.revanced.patcher.PatcherResult
import app.revanced.utils.signing.align.ZipAligner
import app.revanced.utils.signing.align.zip.ZipFile
import app.revanced.utils.signing.align.zip.structures.ZipEntry
import java.io.File

object Aligning {
    fun align(result: PatcherResult, inputFile: File, outputFile: File) {
        logger.info("Aligning ${inputFile.name} to ${outputFile.name}")

        if (outputFile.exists()) outputFile.delete()

        ZipFile(outputFile).use { file ->
            result.dexFiles.forEach {
                file.addEntryCompressData(
                    ZipEntry.createWithName(it.name),
                    it.stream.readBytes()
                )
            }

            result.resourceFile?.let {
                file.copyEntriesFromFileAligned(
                    ZipFile(it),
                    ZipAligner::getEntryAlignment
                )
            }

            file.copyEntriesFromFileAligned(
                ZipFile(inputFile),
                ZipAligner::getEntryAlignment
            )
        }
    }
}
