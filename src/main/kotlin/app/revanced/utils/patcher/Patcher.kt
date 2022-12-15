package app.revanced.utils.patcher

import app.revanced.cli.command.MainCommand
import app.revanced.cli.command.MainCommand.args
import app.revanced.cli.command.MainCommand.logger
import app.revanced.patcher.Patcher
import app.revanced.patcher.data.Context
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.include
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.patch.Patch

fun Patcher.addPatchesFiltered(allPatches: List<Class<out Patch<Context>>>) {
    val packageName = this.context.packageMetadata.packageName
    val packageVersion = this.context.packageMetadata.packageVersion

    val includedPatches = mutableListOf<Class<out Patch<Context>>>()
    allPatches.forEach patchLoop@{ patch ->
        val compatiblePackages = patch.compatiblePackages
        val patchName = patch.patchName
        val args = MainCommand.args.patchArgs?.patchingArgs!!

        val prefix = "Skipping $patchName"

        if (compatiblePackages == null) logger.trace("$patchName: No constraint on packages.")
        else {
            if (!compatiblePackages.any { it.name == packageName }) {
                logger.trace("$prefix: Incompatible with $packageName. This patch is only compatible with ${
                    compatiblePackages.joinToString(
                        ", "
                    ) { it.name }
                }")
                return@patchLoop
            }

            if (!(args.experimental || compatiblePackages.any { it.versions.isEmpty() || it.versions.any { version -> version == packageVersion } })) {
                val compatibleWith = compatiblePackages.joinToString(";") { _package ->
                    "${_package.name}: ${_package.versions.joinToString(", ")}"
                }
                logger.warn("$prefix: Incompatible with version $packageVersion. This patch is only compatible with version $compatibleWith")
                return@patchLoop
            }
        }

        if (args.excludedPatches.contains(patchName)) {
            logger.info("$prefix: Manually excluded")
            return@patchLoop
        } else if ((!patch.include || args.exclusive) && !args.includedPatches.contains(patchName)) {
            logger.info("$prefix: Excluded by default")
            return@patchLoop
        }

        logger.trace("Adding $patchName")
        includedPatches.add(patch)
    }

    this.addPatches(includedPatches)
}

fun Patcher.applyPatchesVerbose() {
    this.executePatches().forEach { (patch, result) ->
        if (result.isSuccess) {
            logger.info("$patch succeeded")
            return@forEach
        }
        logger.error("$patch failed:")
        result.exceptionOrNull()!!.printStackTrace()
    }
}

fun Patcher.mergeFiles() {
    this.addFiles(args.patchArgs?.patchingArgs!!.mergeFiles) { file ->
        logger.info("Merging $file")
    }
}
