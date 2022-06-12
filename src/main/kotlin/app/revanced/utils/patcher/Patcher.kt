package app.revanced.utils.patcher

import app.revanced.cli.command.MainCommand
import app.revanced.cli.command.MainCommand.debugging
import app.revanced.cli.command.MainCommand.patchBundles
import app.revanced.patcher.Patcher
import app.revanced.patcher.data.base.Data
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.include
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.patch.base.Patch
import app.revanced.patcher.util.patch.implementation.JarPatchBundle

fun Patcher.addPatchesFiltered(
    includeFilter: Boolean = false
) {
    val packageName = this.data.packageMetadata.packageName
    val packageVersion = this.data.packageMetadata.packageVersion

    patchBundles.forEach { bundle ->
        val includedPatches = mutableListOf<Class<out Patch<Data>>>()
        JarPatchBundle(bundle).loadPatches().forEach patch@{ patch ->
            val compatiblePackages = patch.compatiblePackages
            val patchName = patch.patchName

            val prefix = "[skipped] $patchName"

            if (includeFilter) {
                if (!MainCommand.includedPatches.contains(patchName)) {
                    println("$prefix: Explicitly excluded.")
                    return@patch
                }
            } else if (!patch.include) {
                println("$prefix: Implicitly excluded.")
                return@patch
            }

            if (compatiblePackages == null) println("$prefix: Missing compatibility annotation. Continuing.")
            else {
                if (!compatiblePackages.any { it.name == packageName }) {
                    println("$prefix: Incompatible package.")
                    return@patch
                }

                if (!(debugging || compatiblePackages.any { it.versions.isEmpty() || it.versions.any { version -> version == packageVersion }})) {
                    println("$prefix: The package version is $packageVersion and is incompatible.")
                    return@patch
                }
            }

            includedPatches.add(patch)
            println("[added] $patchName")
        }
        this.addPatches(includedPatches)
    }
}

fun Patcher.applyPatchesVerbose() {
    this.applyPatches().forEach { (patch, result) ->
        if (result.isSuccess) {
            println("[success] $patch")
            return@forEach
        }
        println("[error] $patch:")
        result.exceptionOrNull()!!.printStackTrace()
    }
}

fun Patcher.mergeFiles() {
    this.addFiles(MainCommand.mergeFiles)
}