package app.revanced.utils.signing.align.stream

import java.io.OutputStream

internal class PeekingFakeStream : OutputStream() {
    private var numberOfBytes: Long = 0

    fun peek() = numberOfBytes

    override fun write(b: Int) {
        numberOfBytes++
    }

    override fun write(b: ByteArray) {
        numberOfBytes += b.size
    }

    override fun write(b: ByteArray, offset: Int, len: Int) {
        numberOfBytes += len
    }
}