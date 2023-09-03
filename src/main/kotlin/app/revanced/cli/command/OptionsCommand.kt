package app.revanced.cli.command

import app.revanced.patcher.PatchBundleLoader
import app.revanced.utils.Options
import app.revanced.utils.Options.setOptions
import picocli.CommandLine
import picocli.CommandLine.Help.Visibility.ALWAYS
import java.io.File
import java.util.logging.Logger

@CommandLine.Command(
    name = "options",
    description = ["Generate options file from patches"],
)
internal object OptionsCommand : Runnable {
    private val logger = Logger.getLogger(OptionsCommand::class.java.name)

    @CommandLine.Parameters(
        description = ["Paths to patch bundles"], arity = "1..*"
    )
    private lateinit var patchBundles: Array<File>

    @CommandLine.Option(
        names = ["-p", "--path"], description = ["Path to patch options JSON file"], showDefaultValue = ALWAYS
    )
    private var filePath: File = File("options.json")

    @CommandLine.Option(
        names = ["-o", "--overwrite"], description = ["Overwrite existing options file"], showDefaultValue = ALWAYS
    )
    private var overwrite: Boolean = false

    @CommandLine.Option(
        names = ["-u", "--update"],
        description = ["Update existing options by adding missing and removing non-existent options"],
        showDefaultValue = ALWAYS
    )
    private var update: Boolean = false

    override fun run() = if (!filePath.exists() || overwrite) with(PatchBundleLoader.Jar(*patchBundles)) {
        if (update && filePath.exists()) setOptions(filePath)

        Options.serialize(this, prettyPrint = true).let(filePath::writeText)
    }
    else logger.severe("Options file already exists, use --override to override it")
}