package app.revanced.utils.patcher

import app.revanced.cli.command.MainCommand
import app.revanced.cli.command.MainCommand.args
import app.revanced.cli.command.MainCommand.logger
import app.revanced.patcher.Patcher
import app.revanced.patcher.data.base.Data
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.include
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.patch.base.Patch
import app.revanced.patcher.util.patch.implementation.JarPatchBundle

fun Patcher.addPatchesFiltered(
    excludePatches: Boolean = false
) {
    val packageName = this.data.packageMetadata.packageName
    val packageVersion = this.data.packageMetadata.packageVersion

    args.patchBundles.forEach { bundle ->
        val includedPatches = mutableListOf<Class<out Patch<Data>>>()
        JarPatchBundle(bundle).loadPatches().forEach patch@{ patch ->
            val compatiblePackages = patch.compatiblePackages
            val patchName = patch.patchName

            val prefix = "Skip $patchName"

            val args = MainCommand.args.pArgs!!

            if (excludePatches && args.excludedPatches.contains(patchName)) {
                logger.info("$prefix: Explicitly excluded")
                return@patch
            } else if (!patch.include) {
                logger.info("$prefix: Implicitly excluded")
                return@patch
            }

            if (compatiblePackages == null) logger.warning("$prefix: Missing compatibility annotation. Continuing.")
            else {
                if (!compatiblePackages.any { it.name == packageName }) {
                    logger.info("$prefix: Incompatible package")
                    return@patch
                }

                if (!(args.experimental || compatiblePackages.any { it.versions.isEmpty() || it.versions.any { version -> version == packageVersion } })) {
                    logger.info("$prefix: The package version is $packageVersion and is incompatible")
                    return@patch
                }
            }

            logger.info("Add $patchName")

            includedPatches.add(patch)
        }
        this.addPatches(includedPatches)
    }
}

fun Patcher.applyPatchesVerbose() {
    this.applyPatches().forEach { (patch, result) ->
        if (result.isSuccess) {
            logger.info("Success: $patch")

            return@forEach
        }
        logger.severe("Error: $patch")

        result.exceptionOrNull()!!.printStackTrace()
    }
}

fun Patcher.mergeFiles() {
    this.addFiles(args.pArgs!!.mergeFiles)
}
