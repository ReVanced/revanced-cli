package app.revanced.cli.patcher

import app.revanced.cli.command.MainCommand.args
import app.revanced.cli.command.MainCommand.logger
import app.revanced.utils.patcher.addPatchesFiltered
import app.revanced.utils.patcher.applyPatchesVerbose
import app.revanced.utils.patcher.mergeFiles
import app.revanced.utils.signing.align.ZipAligner
import app.revanced.utils.signing.align.zip.ZipFile
import app.revanced.utils.signing.align.zip.structures.ZipEntry
import java.io.File
import java.nio.file.Files

internal object Patcher {
    internal fun start(patcher: app.revanced.patcher.Patcher, output: File) {
        val inputFile = args.inputFile
        val args = args.patchArgs?.patchingArgs!!

        // merge files like necessary integrations
        patcher.mergeFiles()
        // add patches, but filter incompatible or excluded patches
        patcher.addPatchesFiltered()
        // apply patches
        patcher.applyPatchesVerbose()

        // write output file
        if (output.exists()) Files.delete(output.toPath())

        val result = patcher.save()
        ZipFile(output).use { outputFile ->
            // replace all dex files
            result.dexFiles.forEach {
                logger.info("Writing dex file ${it.name}")
                outputFile.addEntryCompressData(ZipEntry.createWithName(it.name), it.dexFileInputStream.readAllBytes())
            }

            if (!args.disableResourcePatching) {
                logger.info("Writing resources...")

                outputFile.copyEntriesFromFileAligned(ZipFile(result.resourceFile!!), ZipAligner::getEntryAlignment)
            }

            outputFile.copyEntriesFromFileAligned(ZipFile(inputFile), ZipAligner::getEntryAlignment)
        }
    }
}
