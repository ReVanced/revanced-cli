package app.revanced.cli

import app.revanced.utils.filesystem.FileSystemUtils
import app.revanced.utils.patcher.addPatchesFiltered
import app.revanced.utils.patcher.applyPatchesPrint
import app.revanced.utils.patcher.mergeFiles
import app.revanced.utils.signing.Signer
import java.io.File
import java.io.FileFilter

internal class Patcher {
    internal companion object {
        internal fun start(patcher: app.revanced.patcher.Patcher) {
            // merge files like necessary integrations
            patcher.mergeFiles()
            // add patches, but filter incompatible or excluded patches
            patcher.addPatchesFiltered(includeFilter = MainCommand.includedPatches.isNotEmpty())
            // apply patches
            patcher.applyPatchesPrint()

            // write output file
            val outFile = File(MainCommand.outputPath)
            if (outFile.exists()) outFile.delete()
            MainCommand.inputFile.copyTo(outFile)

            val zipFileSystem = FileSystemUtils(outFile)

            // replace all dex files
            for ((name, data) in patcher.save()) {
                zipFileSystem.replaceFile(name, data.data)
            }

            if (MainCommand.patchResources) {
                for (file in File(MainCommand.cacheDirectory).resolve("build/").listFiles(FileFilter { it.isDirectory })?.first()?.listFiles()!!) {
                    if (!file.isDirectory) {
                        zipFileSystem.replaceFile(file.name, file.readBytes())
                        continue
                    }
                    zipFileSystem.replaceDirectory(file)
                }
            }

            // finally close the stream
            zipFileSystem.close()

            // and sign the apk file
            Signer.signApk(outFile)

            println("[done]")
        }


    }
}
