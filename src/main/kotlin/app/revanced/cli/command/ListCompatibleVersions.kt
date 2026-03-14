package app.revanced.cli.command

import app.revanced.cli.command.PatchesFileInput.Companion.loadPatches
import app.revanced.library.PackageName
import app.revanced.library.VersionMap
import app.revanced.library.mostCommonCompatibleVersions
import picocli.CommandLine
import java.util.concurrent.Callable
import java.util.logging.Logger

@CommandLine.Command(
    name = "list-versions",
    description = [
        "List the most common compatible versions of apps that are compatible " +
                "with the patches from RVP files.",
    ],
    sortOptions = false,
)
internal class ListCompatibleVersions : Callable<Int> {
    private val logger = Logger.getLogger(this::class.java.name)

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1..*")
    private lateinit var patchesFileInputs: List<PatchesFileInput>

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

    override fun call(): Int {
        fun VersionMap.buildVersionsString(): String {
            if (isEmpty()) return "Any"

            fun buildPatchesCountString(count: Int) =
                if (count == 1) "1 patch" else "$count patches"

            return entries.joinToString("\n") { (version, count) ->
                "$version (${buildPatchesCountString(count)})"
            }
        }

        fun buildString(entry: Map.Entry<PackageName, VersionMap>) = buildString {
            val (name, versions) = entry
            appendLine("Package name: $name")
            appendLine("Most common compatible versions:")
            appendLine(versions.buildVersionsString().prependIndent("\t"))
        }

        val patches = loadPatches(patchesFileInputs) ?: return -1

        patches.mostCommonCompatibleVersions(
            packageNames,
            countUnusedPatches,
        ).entries.joinToString("\n", transform = ::buildString).let(logger::info)

        return 0
    }
}
