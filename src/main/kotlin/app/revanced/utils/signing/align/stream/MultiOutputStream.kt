package app.revanced.utils.signing.align.stream

import java.io.OutputStream

internal class MultiOutputStream(
    private val streams: Iterable<OutputStream>,
) : OutputStream() {
    override fun write(b: ByteArray, off: Int, len: Int) = streams.forEach {
        it.write(b, off, len)
    }

    override fun write(b: ByteArray) = streams.forEach {
        it.write(b)
    }

    override fun write(b: Int) = streams.forEach {
        it.write(b)
    }

}