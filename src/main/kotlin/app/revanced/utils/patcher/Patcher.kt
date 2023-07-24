package app.revanced.utils.patcher

import app.revanced.cli.command.MainCommand.args
import app.revanced.cli.command.MainCommand.logger
import app.revanced.cli.command.PatchList
import app.revanced.patcher.Patcher
import app.revanced.patcher.data.Context
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.include
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.patch.Patch

fun Patcher.addPatchesFiltered(allPatches: PatchList) {
    val packageName = this.context.packageMetadata.packageName
    val packageVersion = this.context.packageMetadata.packageVersion

    val includedPatches = mutableListOf<Class<out Patch<Context>>>()
    allPatches.forEach patchLoop@{ patch ->
        val compatiblePackages = patch.compatiblePackages
        val args = args.patchArgs?.patchingArgs!!

        val prefix = "Skipping ${patch.patchName}"

        if (compatiblePackages == null) logger.trace("${patch.patchName}: No package constraints.")
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
                logger.warn("$prefix: Incompatible with version $packageVersion. This patch is only compatible with $compatibleWith")
                return@patchLoop
            }
        }

        val kebabCasedPatchName = patch.patchName.lowercase().replace(" ", "-")
        if (args.excludedPatches.contains(kebabCasedPatchName)) {
            logger.info("$prefix: Manually excluded")
            return@patchLoop
        } else if ((!patch.include || args.exclusive) && !args.includedPatches.contains(kebabCasedPatchName)) {
            logger.info("$prefix: Excluded by default")
            return@patchLoop
        }

        logger.trace("Adding ${patch.patchName}")
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
    this.addIntegrations(args.patchArgs?.patchingArgs!!.mergeFiles) { file ->
        logger.info("Merging $file")
    }
}
