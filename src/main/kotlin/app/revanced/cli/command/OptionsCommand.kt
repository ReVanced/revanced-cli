package app.revanced.cli.command

import app.revanced.library.Options
import app.revanced.library.Options.setOptions
import app.revanced.patcher.PatchBundleLoader
import picocli.CommandLine
import picocli.CommandLine.Help.Visibility.ALWAYS
import java.io.File
import java.util.logging.Logger

@CommandLine.Command(
    name = "options",
    description = ["Generate options file from patches."],
)
internal object OptionsCommand : Runnable {
    private val logger = Logger.getLogger(OptionsCommand::class.java.name)

    @CommandLine.Parameters(
        description = ["Paths to patch bundles."],
        arity = "1..*",
    )
    private lateinit var patchBundles: Array<File>

    @CommandLine.Option(
        names = ["-p", "--path"],
        description = ["Path to patch options JSON file."],
        showDefaultValue = ALWAYS,
    )
    private var filePath: File = File("options.json")

    @CommandLine.Option(
        names = ["-o", "--overwrite"],
        description = ["Overwrite existing options file."],
        showDefaultValue = ALWAYS,
    )
    private var overwrite: Boolean = false

    @CommandLine.Option(
        names = ["-u", "--update"],
        description = ["Update existing options by adding missing and removing non-existent options."],
        showDefaultValue = ALWAYS,
    )
    private var update: Boolean = false

    override fun run() =
        try {
            PatchBundleLoader.Jar(*patchBundles).let { patches ->
                val exists = filePath.exists()
                if (!exists || overwrite) {
                    if (exists && update) patches.setOptions(filePath)

                    Options.serialize(patches, prettyPrint = true).let(filePath::writeText)
                } else {
                    throw OptionsFileAlreadyExistsException()
                }
            }
        } catch (ex: OptionsFileAlreadyExistsException) {
            logger.severe("Options file already exists, use --overwrite to override it")
        }

    class OptionsFileAlreadyExistsException : Exception()
}
