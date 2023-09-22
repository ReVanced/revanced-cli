package app.revanced.lib

import app.revanced.patcher.PatchSet

/**
 * Utility functions for working with patches.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
object PatchUtils {
    /**
     * Get the version that is most common for [packageName] in the supplied set of [patches].
     *
     * @param patches The set of patches to check.
     * @param packageName The name of the compatible package.
     * @return The most common version of.
     */
    fun getMostCommonCompatibleVersion(patches: PatchSet, packageName: String) = patches
        .mapNotNull {
            // Map all patches to their compatible packages with version constraints.
            it.compatiblePackages?.firstOrNull { compatiblePackage ->
                compatiblePackage.name == packageName && compatiblePackage.versions?.isNotEmpty() == true
            }
        }
        .flatMap { it.versions!! }
        .groupingBy { it }
        .eachCount()
        .maxByOrNull { it.value }?.key
}