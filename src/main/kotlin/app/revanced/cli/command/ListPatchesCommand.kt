package app.revanced.cli.command

import app.revanced.cli.command.PatchesFileInput.Companion.loadPatches
import app.revanced.patcher.patch.Package
import app.revanced.patcher.patch.Patch
import picocli.CommandLine.*
import picocli.CommandLine.Help.Visibility.ALWAYS
import java.util.concurrent.Callable
import java.util.logging.Logger
import app.revanced.patcher.patch.Option as PatchOption

@Command(
    name = "list-patches",
    description = ["List patches from supplied RVP files."],
    sortOptions = false,
)
internal object ListPatchesCommand : Callable<Int> {
    private val logger = Logger.getLogger(this::class.java.name)

    @ArgGroup(exclusive = false, multiplicity = "1..*")
    private lateinit var patchesFileInputs: List<PatchesFileInput>

    @Option(
        names = ["--descriptions"],
        description = ["List their descriptions."],
        showDefaultValue = ALWAYS,
    )
    private var showDescriptions: Boolean = true

    @Option(
        names = ["--packages"],
        description = ["List the packages the patches are compatible with."],
        showDefaultValue = ALWAYS,
    )
    private var showPackages: Boolean = false

    @Option(
        names = ["--versions"],
        description = ["List the versions of the apps the patches are compatible with."],
        showDefaultValue = ALWAYS,
    )
    private var showVersions: Boolean = false

    @Option(
        names = ["--options"],
        description = ["List the options of the patches."],
        showDefaultValue = ALWAYS,
    )
    private var showOptions: Boolean = false

    @Option(
        names = ["--universal-patches"],
        description = ["List patches which are compatible with any app."],
        showDefaultValue = ALWAYS,
    )
    private var showUniversalPatches: Boolean = true

    @Option(
        names = ["--index"],
        description = ["List the index of each patch in relation to the supplied RVP files."],
        showDefaultValue = ALWAYS,
    )
    private var showIndex: Boolean = true

    @Option(
        names = ["--filter-package-name"],
        description = ["Filter patches by package name."],
    )
    private var packageName: String? = null

    override fun call(): Int {
        fun Package.buildString(): String {
            val (name, versions) = this

            return buildString {
                if (showVersions && versions != null) {
                    appendLine("Package name: $name")
                    appendLine("Compatible versions:")
                    append(versions.joinToString("\n") { version -> version }.prependIndent("\t"))
                } else {
                    append("Package name: $name")
                }
            }
        }

        fun PatchOption<*>.buildString() = buildString {
            appendLine("Name: $name")
            description?.let { appendLine("Description: $it") }
            appendLine("Required: $required")
            default?.let { append("Default: $it") }

            values?.let { values ->
                appendLine("\nPossible values:")
                append(
                    values.map { "${it.value} (${it.key})" }.joinToString("\n").prependIndent("\t")
                )
            }

            append("\nType: $type")
        }

        fun IndexedValue<Patch>.buildString() = let { (index, patch) ->
            buildString {
                if (showIndex) appendLine("Index: $index")

                append("Name: ${patch.name}")

                if (showDescriptions) patch.description?.let { append("\nDescription: $it") }

                append("\nEnabled: ${patch.use}")

                if (showOptions && patch.options.isNotEmpty()) {
                    appendLine("\nOptions:")
                    append(
                        patch.options.values.joinToString("\n\n") { option ->
                            option.buildString()
                        }.prependIndent("\t"),
                    )
                }

                if (showPackages && patch.compatiblePackages != null) {
                    appendLine("\nCompatible packages:")
                    append(
                        patch.compatiblePackages!!.joinToString("\n") {
                            it.buildString()
                        }.prependIndent("\t"),
                    )
                }
            }
        }

        fun Patch.filterCompatiblePackages(name: String) =
            compatiblePackages?.any { (compatiblePackageName, _) -> compatiblePackageName == name }
                ?: showUniversalPatches


        val patches = loadPatches(patchesFileInputs)?.withIndex()?.toList() ?: return -1

        val filtered =
            packageName?.let { patches.filter { (_, patch) -> patch.filterCompatiblePackages(it) } }
                ?: patches

        if (filtered.isNotEmpty()) logger.info(filtered.joinToString("\n\n") { it.buildString() })

        return 0
    }
}
