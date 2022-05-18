package app.revanced.cli

import app.revanced.patch.Patches
import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.base.Data
import app.revanced.patcher.extensions.findAnnotationRecursively
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
            for ((patch, result) in patcher.applyPatches()) {
                println("[error: ${result.isFailure}] $patch")
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
                for (file in File(MainCommand.cacheDirectory).resolve("build/").listFiles()?.first()?.listFiles()!!) {
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

        private fun app.revanced.patcher.Patcher.addPatchesFiltered() {
            val packageName = this.packageName
            val packageVersion = this.packageVersion

            val checkInclude = MainCommand.includedPatches.isNotEmpty()

            MainCommand.patchBundles.forEach { bundle ->
                val includedPatches = mutableListOf<Patch<Data>>()
                Patches.load(bundle).forEach patch@{ it ->
                    val patch = it.getDeclaredConstructor().newInstance()

                    val filterOutPatches = true

                    val compatibilityAnnotation = patch.javaClass.findAnnotationRecursively(Compatibility::class.java)

                    val patchName =
                        patch.javaClass.findAnnotationRecursively(Name::class.java)?.name ?: Name::class.java.name

                    if (checkInclude && !MainCommand.includedPatches.contains(patchName)) {
                        return@patch
                    }

                    if (filterOutPatches) {
                        if (compatibilityAnnotation == null || !(compatibilityAnnotation.compatiblePackages.any { packageMetadata ->
                                packageMetadata.name == packageName && packageMetadata.versions.any {
                                    it == packageVersion
                                }
                            })) {
                            // TODO: misleading error message
                            println("[Skipped] $patchName: Incompatible with current package.")
                            return@patch
                        }
                    }


                    println("[loaded] $patchName")
                    includedPatches.add(patch)

                }
                this.addPatches(includedPatches)
            }
        }
    }
}
