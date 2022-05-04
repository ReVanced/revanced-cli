package app.revanced.cli

import app.revanced.patch.PatchLoader
import app.revanced.patch.Patches
import app.revanced.patcher.data.base.Data
import app.revanced.patcher.patch.base.Patch
import app.revanced.utils.filesystem.FileSystemUtils
import app.revanced.utils.signing.Signer
import java.io.File

internal class Patcher {
    internal companion object {
        internal fun start(patcher: app.revanced.patcher.Patcher) {
            // merge files like necessary integrations
            patcher.addFiles(MainCommand.mergeFiles)
            // add patches, but filter incompatible or excluded patches
            patcher.addPatchesFiltered()
            // apply patches
            for (patchResult in patcher.applyPatches {
                println("Applying: $it")
            }) {
                println(patchResult)
            }

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
                for (file in File(MainCommand.cacheDirectory).resolve("build/").listFiles().first().listFiles()) {
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
        }

        private fun app.revanced.patcher.Patcher.addPatchesFiltered() {
            // TODO: get package metadata (outside of this method) for apk file which needs to be patched
            val packageName = this.packageName
            val packageVersion = this.packageVersion

            val checkInclude = MainCommand.includedPatches.isNotEmpty()

            MainCommand.patchBundles.forEach { bundle ->
                PatchLoader.injectPatches(bundle)
                val includedPatches = mutableListOf<Patch<Data>>()
                Patches.loadPatches().forEach patch@{
                    val patch = it()

                    // TODO: filter out incompatible patches with package metadata
                    val filterOutPatches = true
                    if (filterOutPatches && !patch.metadata.compatiblePackages.any { packageMetadata ->
                            packageMetadata.name == packageName && packageMetadata.versions.any {
                                it == packageVersion
                            }
                        }) {
                        // TODO: report to stdout
                        return@patch
                    }

                    if (checkInclude && !MainCommand.includedPatches.contains(patch.metadata.shortName)) {
                        return@patch
                    }

                    // TODO: report to stdout
                    includedPatches.add(patch)

                }
                this.addPatches(includedPatches)
            }
        }
    }
}
