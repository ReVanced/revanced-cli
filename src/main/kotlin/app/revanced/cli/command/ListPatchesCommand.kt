package app.revanced.cli.command

import app.revanced.patcher.PatchBundleLoader
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.description
import app.revanced.patcher.extensions.PatchExtensions.options
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.patch.PatchClass
import app.revanced.patcher.patch.PatchOption
import picocli.CommandLine
import picocli.CommandLine.Help.Visibility.ALWAYS
import java.io.File


@CommandLine.Command(name = "list-patches", description = ["List patches from supplied patch bundles"])
class ListPatchesCommand : Runnable {
    @CommandLine.Parameters(
        description = ["Paths to patch bundles"],
        arity = "1..*"
    )
    lateinit var patchBundles: Array<File>

    @CommandLine.Option(
        names = ["-d", "--with-descriptions"],
        description = ["List their descriptions"],
        showDefaultValue = ALWAYS
    )
    var withDescriptions: Boolean = true

    @CommandLine.Option(
        names = ["-p", "--with-packages"],
        description = ["List the packages the patches are compatible with"],
        showDefaultValue = ALWAYS
    )
    var withPackages: Boolean = false

    @CommandLine.Option(
        names = ["-v", "--with-versions"],
        description = ["List the versions of the packages the patches are compatible with"],
        showDefaultValue = ALWAYS
    )
    var withVersions: Boolean = false

    @CommandLine.Option(
        names = ["-o", "--with-options"],
        description = ["List the options of the patches"],
        showDefaultValue = ALWAYS
    )
    var withOptions: Boolean = false

    override fun run() {
        fun Package.buildString() = buildString {
            if (withVersions && versions.isNotEmpty()) {
                appendLine("Package name: $name")
                appendLine("Compatible versions:")
                append(versions.joinToString("\n") { version -> version }.prependIndent("\t"))
            } else
                append("Package name: $name")
        }

        fun PatchOption<*>.buildString() = buildString {
            appendLine("Title: $title")
            appendLine("Description: $description")

            value?.let {
                appendLine("Key: $key")
                append("Value: $it")
            } ?: append("Key: $key")
        }

        fun PatchClass.buildString() = buildString {
            append("Name: $patchName")

            if (withDescriptions) append("\nDescription: $description")

            if (withOptions && options != null) {
                appendLine("\nOptions:")
                append(
                    options!!.joinToString("\n\n") { option -> option.buildString() }.prependIndent("\t")
                )
            }

            if (withPackages && compatiblePackages != null) {
                appendLine("\nCompatible packages:")
                append(
                    compatiblePackages!!.joinToString("\n") { it.buildString() }.prependIndent("\t")
                )
            }
        }

        MainCommand.logger.info(PatchBundleLoader.Jar(*patchBundles).joinToString("\n\n") { it.buildString() })
    }
}