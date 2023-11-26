package app.revanced.cli.command

import app.revanced.patcher.PatchBundleLoader
import app.revanced.patcher.patch.Patch
import app.revanced.patcher.patch.options.PatchOption
import picocli.CommandLine.*
import picocli.CommandLine.Help.Visibility.ALWAYS
import java.io.File
import java.util.logging.Logger

@Command(
    name = "list-patches",
    description = ["List patches from supplied patch bundles."],
)
internal object ListPatchesCommand : Runnable {
    private val logger = Logger.getLogger(ListPatchesCommand::class.java.name)

    @Parameters(
        description = ["Paths to patch bundles."],
        arity = "1..*",
    )
    private lateinit var patchBundles: Array<File>

    @Option(
        names = ["-d", "--with-descriptions"],
        description = ["List their descriptions."],
        showDefaultValue = ALWAYS,
    )
    private var withDescriptions: Boolean = true

    @Option(
        names = ["-p", "--with-packages"],
        description = ["List the packages the patches are compatible with."],
        showDefaultValue = ALWAYS,
    )
    private var withPackages: Boolean = false

    @Option(
        names = ["-v", "--with-versions"],
        description = ["List the versions of the apps the patches are compatible with."],
        showDefaultValue = ALWAYS,
    )
    private var withVersions: Boolean = false

    @Option(
        names = ["-o", "--with-options"],
        description = ["List the options of the patches."],
        showDefaultValue = ALWAYS,
    )
    private var withOptions: Boolean = false

    @Option(
        names = ["-u", "--with-universal-patches"],
        description = ["List patches which are compatible with any app."],
        showDefaultValue = ALWAYS,
    )
    private var withUniversalPatches: Boolean = true

    @Option(
        names = ["-i", "--index"],
        description = ["List the index of each patch in relation to the supplied patch bundles."],
        showDefaultValue = ALWAYS,
    )
    private var withIndex: Boolean = true

    @Option(
        names = ["-f", "--filter-package-name"],
        description = ["Filter patches by package name."],
    )
    private var packageName: String? = null

    override fun run() {
        fun Patch.CompatiblePackage.buildString() =
            buildString {
                if (withVersions && versions != null) {
                    appendLine("Package name: $name")
                    appendLine("Compatible versions:")
                    append(versions!!.joinToString("\n") { version -> version }.prependIndent("\t"))
                } else {
                    append("Package name: $name")
                }
            }

        fun PatchOption<*>.buildString() =
            buildString {
                appendLine("Title: $title")
                description?.let { appendLine("Description: $it") }
                default?.let {
                    appendLine("Key: $key")
                    append("Default: $it")
                } ?: append("Key: $key")

                values?.let { values ->
                    appendLine("\nValid values:")
                    append(values.map { "${it.value} (${it.key})" }.joinToString("\n").prependIndent("\t"))
                }
            }

        fun IndexedValue<Patch<*>>.buildString() =
            let { (index, patch) ->
                buildString {
                    if (withIndex) appendLine("Index: $index")

                    append("Name: ${patch.name}")

                    if (withDescriptions) append("\nDescription: ${patch.description}")

                    if (withOptions && patch.options.isNotEmpty()) {
                        appendLine("\nOptions:")
                        append(
                            patch.options.values.joinToString("\n\n") { option ->
                                option.buildString()
                            }.prependIndent("\t"),
                        )
                    }

                    if (withPackages && patch.compatiblePackages != null) {
                        appendLine("\nCompatible packages:")
                        append(
                            patch.compatiblePackages!!.joinToString("\n") {
                                it.buildString()
                            }.prependIndent("\t"),
                        )
                    }
                }
            }

        fun Patch<*>.filterCompatiblePackages(name: String) =
            compatiblePackages?.any { it.name == name }
                ?: withUniversalPatches

        val patches = PatchBundleLoader.Jar(*patchBundles).withIndex().toList()

        val filtered =
            packageName?.let { patches.filter { (_, patch) -> patch.filterCompatiblePackages(it) } } ?: patches

        if (filtered.isNotEmpty()) logger.info(filtered.joinToString("\n\n") { it.buildString() })
    }
}
