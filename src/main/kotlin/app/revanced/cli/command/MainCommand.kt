package app.revanced.cli.command

import app.revanced.cli.logging.impl.DefaultCliLogger
import app.revanced.patcher.patch.PatchClass
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.IVersionProvider
import java.util.*

fun main(args: Array<String>) {
    CommandLine(MainCommand).execute(*args)
}

internal typealias PatchList = List<PatchClass>

internal val logger = DefaultCliLogger()

object CLIVersionProvider : IVersionProvider {
    override fun getVersion(): Array<String> {
        Properties().apply {
            load(MainCommand::class.java.getResourceAsStream("/app/revanced/cli/version.properties"))
        }.let {
            return arrayOf("ReVanced CLI v${it.getProperty("version")}")
        }
    }
}

@Command(
    name = "revanced-cli",
    description = ["Command line application to use ReVanced"],
    mixinStandardHelpOptions = true,
    versionProvider = CLIVersionProvider::class,
    subcommands = [
        ListPatchesCommand::class,
        PatchCommand::class,
        UninstallCommand::class,
        OptionsCommand::class,
    ]
)
internal object MainCommand