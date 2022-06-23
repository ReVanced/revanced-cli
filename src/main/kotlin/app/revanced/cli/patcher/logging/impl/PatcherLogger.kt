package app.revanced.cli.patcher.logging.impl

import app.revanced.cli.logging.impl.DefaultCliLogger
import java.util.logging.Logger

internal object PatcherLogger : app.revanced.patcher.logging.Logger{
    private val logger = DefaultCliLogger(Logger.getLogger(app.revanced.patcher.Patcher::javaClass.name))

    override fun error(msg: String) = logger.error(msg)
    override fun info(msg: String) = logger.info(msg)
    override fun warn(msg: String)= logger.warn(msg)
    override fun trace(msg: String)= logger.trace(msg)
}