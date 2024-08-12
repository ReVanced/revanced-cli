package app.revanced.cli.command

import app.revanced.library.PackageName
import app.revanced.library.VersionMap
import app.revanced.library.mostCommonCompatibleVersions
import app.revanced.patcher.patch.loadPatchesFromJar
import picocli.CommandLine
import java.io.File
import java.util.logging.Logger

@CommandLine.Command(
    name = "list-versions",
    description = [
        "List the most common compatible versions of apps that are compatible " +
            "with the patches in the supplied patch bundles.",
    ],
)
internal class ListCompatibleVersions : Runnable {
    private val logger = Logger.getLogger(this::class.java.name)

    @CommandLine.Parameters(
        description = ["Paths to patch bundles."],
        arity = "1..*",
    )
    private lateinit var patchBundles: Set<File>

    @CommandLine.Option(
        names = ["-f", "--filter-package-names"],
        description = ["Filter patches by package name."],
    )
    private var packageNames: Set<String>? = null

    @CommandLine.Option(
        names = ["-u", "--count-unused-patches"],
        description = ["Count patches that are not used by default."],
        showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
    )
    private var countUnusedPatches: Boolean = false

    override fun run() {
        fun VersionMap.buildVersionsString(): String {
            if (isEmpty()) return "Any"

            fun buildPatchesCountString(count: Int) = if (count == 1) "1 patch" else "$count patches"

            return entries.joinToString("\n") { (version, count) ->
                "$version (${buildPatchesCountString(count)})"
            }
        }

        fun buildString(entry: Map.Entry<PackageName, VersionMap>) =
            buildString {
                val (name, versions) = entry
                appendLine("Package name: $name")
                appendLine("Most common compatible versions:")
                appendLine(versions.buildVersionsString().prependIndent("\t"))
            }

        val patches = loadPatchesFromJar(patchBundles)

        patches.mostCommonCompatibleVersions(
            packageNames,
            countUnusedPatches,
        ).entries.joinToString("\n", transform = ::buildString).let(logger::info)
    }
}
