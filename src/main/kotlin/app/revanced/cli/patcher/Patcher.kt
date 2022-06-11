package app.revanced.cli.patcher

import app.revanced.cli.command.MainCommand.cacheDirectory
import app.revanced.cli.command.MainCommand.disableResourcePatching
import app.revanced.cli.command.MainCommand
import app.revanced.cli.command.MainCommand.includedPatches
import app.revanced.utils.filesystem.ZipFileSystemUtils
import app.revanced.utils.patcher.addPatchesFiltered
import app.revanced.utils.patcher.applyPatchesVerbose
import app.revanced.utils.patcher.mergeFiles
import java.io.File
import java.nio.file.Files

internal object Patcher {
    internal fun start(patcher: app.revanced.patcher.Patcher, output: File) {
        // merge files like necessary integrations
        patcher.mergeFiles()
        // add patches, but filter incompatible or excluded patches
        patcher.addPatchesFiltered(includeFilter = includedPatches.isNotEmpty())
        // apply patches
        patcher.applyPatchesVerbose()

        // write output file
        if (output.exists()) Files.delete(output.toPath())
        MainCommand.inputFile.copyTo(output)

        ZipFileSystemUtils(output).use { fileSystem ->
            // replace all dex files
            val result = patcher.save()
            result.dexFiles.forEach {
                fileSystem.write(it.name, it.memoryDataStore.data)
            }

            // write resources
            if (!disableResourcePatching) {
                fileSystem.writePathRecursively(File(cacheDirectory).resolve("build").toPath())
                fileSystem.uncompress(*result.doNotCompress!!.toTypedArray())
            }
        }
    }
}
