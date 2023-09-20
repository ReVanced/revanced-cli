package app.revanced.lib.logging

import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.SimpleFormatter

@Suppress("MemberVisibilityCanBePrivate")
object Logger {
    private val rootLogger = java.util.logging.Logger.getLogger("")

    /**
     * Sets the format for the logger.
     *
     * @param format The format to use.
     */
    fun setFormat(format: String = "%4\$s: %5\$s %n") {
        System.setProperty("java.util.logging.SimpleFormatter.format", format)
    }

    /**
     * Removes all handlers from the logger.
     */
    fun removeAllHandlers() {
        rootLogger.let { logger ->
            logger.handlers.forEach { handler ->
                handler.close()
                logger.removeHandler(handler)
            }
        }
    }

    /**
     * Adds a handler to the logger.
     *
     * @param publishHandler The handler for publishing the log.
     * @param flushHandler The handler for flushing the log.
     * @param closeHandler The handler for closing the log.
     */
    fun addHandler(
        publishHandler: (log: String, level: Level, loggerName: String?) -> Unit,
        flushHandler: () -> Unit,
        closeHandler: () -> Unit
    ) = object : Handler() {
        override fun publish(record: LogRecord) = publishHandler(
            formatter.format(record),
            record.level,
            record.loggerName
        )

        override fun flush() = flushHandler()

        override fun close() = closeHandler()
    }.also {
        it.level = Level.ALL
        it.formatter = SimpleFormatter()
    }.let(rootLogger::addHandler)

    /**
     * Log to "standard" (error) output streams.
     */
    fun setDefault() {
        setFormat()
        removeAllHandlers()

        val publishHandler = handler@{ log: String, level: Level, loggerName: String? ->
            if (loggerName?.startsWith("app.revanced") != true) return@handler

            log.toByteArray().let {
                if (level.intValue() > Level.WARNING.intValue())
                    System.err.write(it)
                else
                    System.out.write(it)
            }
        }

        val flushHandler = {
            System.out.flush()
            System.err.flush()
        }

        addHandler(publishHandler, flushHandler, flushHandler)
    }
}