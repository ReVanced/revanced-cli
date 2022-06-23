package app.revanced.cli.logging.impl

import app.revanced.cli.command.MainCommand
import app.revanced.cli.logging.CliLogger
import java.util.logging.Logger

internal class DefaultCliLogger(
    private val logger: Logger = Logger.getLogger(MainCommand::javaClass.name)
) : CliLogger {
    companion object {
        init {
            System.setProperty("java.util.logging.SimpleFormatter.format", "%4\$s: %5\$s %n")
        }
    }

    override fun error(msg: String) = logger.severe(msg)
    override fun info(msg: String) = logger.info(msg)
    override fun trace(msg: String) = logger.finest(msg)
    override fun warn(msg: String) = logger.warning(msg)
}