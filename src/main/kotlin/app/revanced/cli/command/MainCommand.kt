package app.revanced.cli.command

import app.revanced.cli.command.utility.UtilityCommand
import app.revanced.patcher.patch.PatchClass
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.IVersionProvider
import java.util.*
import java.util.logging.*


fun main(args: Array<String>) {
    System.setProperty("java.util.logging.SimpleFormatter.format", "%4\$s: %5\$s %n")
    Logger.getLogger("").apply {
        handlers.forEach {
            it.close()
            removeHandler(it)
        }

        object : Handler() {
            override fun publish(record: LogRecord) = formatter.format(record).toByteArray().let {
                if (record.level.intValue() > Level.INFO.intValue())
                    System.err.write(it)
                else
                    System.out.write(it)
            }

            override fun flush() {
                System.out.flush()
                System.err.flush()
            }

            override fun close() = flush()
        }.also {
            it.level = Level.ALL
            it.formatter = SimpleFormatter()
        }.let(::addHandler)
    }

    CommandLine(MainCommand).execute(*args)
}

internal typealias PatchList = List<PatchClass>

private object CLIVersionProvider : IVersionProvider {
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
        OptionsCommand::class,
        UtilityCommand::class,
    ]
)
private object MainCommand