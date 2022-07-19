package app.revanced.utils.patcher

import app.revanced.cli.command.MainCommand
import app.revanced.cli.command.MainCommand.args
import app.revanced.cli.command.MainCommand.logger
import app.revanced.patcher.Patcher
import app.revanced.patcher.data.Data
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.include
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.patch.Patch
import app.revanced.patcher.util.patch.implementation.JarPatchBundle

fun Patcher.addPatchesFiltered() {
    val packageName = this.data.packageMetadata.packageName
    val packageVersion = this.data.packageMetadata.packageVersion

    args.sArgs?.patchBundles!!.forEach { bundle ->
        val includedPatches = mutableListOf<Class<out Patch<Data>>>()
        JarPatchBundle(bundle).loadPatches().forEach patch@{ patch ->
            val compatiblePackages = patch.compatiblePackages
            val patchName = patch.patchName

            val prefix = "Skipping $patchName"

            val args = MainCommand.args.sArgs?.pArgs!!

            if (args.excludedPatches.contains(patchName)) {
                logger.info("$prefix: Explicitly excluded")
                return@patch
            } else if ((!patch.include || args.defaultExclude) && !args.includedPatches.contains(patchName)) {
                logger.info("$prefix: Not explicitly included")
                return@patch
            }

            if (compatiblePackages == null) logger.warn("$prefix: Missing compatibility annotation. Continuing.")
            else {
                if (!compatiblePackages.any { it.name == packageName }) {
                    logger.warn("$prefix: Incompatible with $packageName. This patch is only compatible with ${
                        compatiblePackages.joinToString(
                            ", "
                        ) { it.name }
                    }")
                    return@patch
                }

                if (!(args.experimental || compatiblePackages.any { it.versions.isEmpty() || it.versions.any { version -> version == packageVersion } })) {
                    val compatibleWith = compatiblePackages.map { _package ->
                        "${_package.name}: ${_package.versions.joinToString(", ")}"
                    }.joinToString(";")
                    logger.warn("$prefix: Incompatible with version $packageVersion. This patch is only compatible with version $compatibleWith")
                    return@patch
                }
            }

            logger.trace("Adding $patchName")
            includedPatches.add(patch)
        }
        this.addPatches(includedPatches)
    }
}

fun Patcher.applyPatchesVerbose() {
    this.applyPatches().forEach { (patch, result) ->
        if (result.isSuccess) {
            logger.info("$patch succeeded")
            return@forEach
        }
        logger.error("$patch failed:")
        result.exceptionOrNull()!!.printStackTrace()
    }
}

fun Patcher.mergeFiles() {
    this.addFiles(args.sArgs?.pArgs!!.mergeFiles) { file ->
        logger.info("Merging $file")
    }
}
