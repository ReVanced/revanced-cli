package app.revanced.cli.patcher

import app.revanced.cli.command.MainCommand.args
import app.revanced.cli.command.MainCommand.logger
import app.revanced.patcher.data.Data
import app.revanced.patcher.patch.Patch
import app.revanced.utils.filesystem.ZipFileSystemUtils
import app.revanced.utils.patcher.addPatchesFiltered
import app.revanced.utils.patcher.applyPatchesVerbose
import app.revanced.utils.patcher.mergeFiles
import java.io.File
import java.nio.file.Files

internal object Patcher {
    internal fun start(patcher: app.revanced.patcher.Patcher, output: File, allPatches: List<Class<out Patch<Data>>>) {
        val inputFile = args.inputFile
        val args = args.patchArgs?.patchingArgs!!

        // merge files like necessary integrations
        patcher.mergeFiles()
        // add patches, but filter incompatible or excluded patches
        patcher.addPatchesFiltered(allPatches)
        // apply patches
        patcher.applyPatchesVerbose()

        // write output file
        if (output.exists()) Files.delete(output.toPath())
        inputFile.copyTo(output)

        val result = patcher.save()
        ZipFileSystemUtils(output).use { outputFileSystem ->
            // replace all dex files
            result.dexFiles.forEach {
                logger.info("Writing dex file ${it.name}")
                outputFileSystem.write(it.name, it.stream.readAllBytes())
            }

            result.resourceFile?.let {
                logger.info("Writing resources...")

                ZipFileSystemUtils(it).use { resourceFileSystem ->
                    val resourceFiles = resourceFileSystem.getFile(File.separator)
                    outputFileSystem.writePathRecursively(resourceFiles)
                }
            }

            result.doNotCompress?.let { outputFileSystem.uncompress(*it.toTypedArray()) }
        }
    }
}
