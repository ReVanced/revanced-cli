package app.revanced.cli.patcher

import app.revanced.cli.command.MainCommand.args
import app.revanced.patcher.PatcherResult
import app.revanced.patcher.data.Context
import app.revanced.patcher.patch.Patch
import app.revanced.utils.patcher.addPatchesFiltered
import app.revanced.utils.patcher.applyPatchesVerbose
import app.revanced.utils.patcher.mergeFiles

internal object Patcher {
    internal fun start(
        patcher: app.revanced.patcher.Patcher,
        allPatches: List<Class<out Patch<Context>>>
    ): PatcherResult {
        // merge files like necessary integrations
        patcher.mergeFiles()
        // add patches, but filter incompatible or excluded patches
        patcher.addPatchesFiltered(allPatches)
        // apply patches
        patcher.applyPatchesVerbose()
		
        args.patchArgs?.patchingArgs?.let { args ->
            args.ripLibs.forEach {
                logger.info("Ripping $it libs")
                try {
                    outputFileSystem.deleteRecursively("lib/$it")
                } catch (_e: Exception) {
                }
            }
        }

        return patcher.save()
    }
}
