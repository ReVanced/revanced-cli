package app.revanced.cli.logging.impl

import java.io.OutputStream
import java.util.logging.Formatter
import java.util.logging.LogRecord
import java.util.logging.StreamHandler

internal class FlushingStreamHandler(out: OutputStream, format: Formatter) : StreamHandler(out, format) {
    override fun publish(record: LogRecord) {
        super.publish(record)
        flush()
    }
}