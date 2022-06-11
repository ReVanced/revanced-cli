package app.revanced.utils.signing.align

import app.revanced.utils.signing.align.stream.MultiOutputStream
import app.revanced.utils.signing.align.stream.PeekingFakeStream
import java.io.BufferedOutputStream
import java.io.File
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

internal object ZipAligner {
    fun align(input: File, output: File, alignment: Int = 4) {
        val zipFile = ZipFile(input)

        val entries: Enumeration<out ZipEntry?> = zipFile.entries()

        // fake
        val peekingFakeStream = PeekingFakeStream()
        val fakeOutputStream = ZipOutputStream(peekingFakeStream)
        // real
        val zipOutputStream = ZipOutputStream(BufferedOutputStream(output.outputStream()))

        val multiOutputStream = MultiOutputStream(
            listOf(
                fakeOutputStream, // fake, used to add the data to the fake stream
                zipOutputStream // real
            )
        )

        var bias = 0
        while (entries.hasMoreElements()) {
            var padding = 0

            val entry: ZipEntry = entries.nextElement()!!
            // fake, used to calculate the file offset of the entry
            fakeOutputStream.putNextEntry(entry)

            if (entry.size == entry.compressedSize) {
                val fileOffset = peekingFakeStream.peek()
                val newOffset = fileOffset + bias
                padding = ((alignment - (newOffset % alignment)) % alignment).toInt()

                // real
                entry.extra = if (entry.extra == null) ByteArray(padding)
                else Arrays.copyOf(entry.extra, entry.extra.size + padding)
            }

            zipOutputStream.putNextEntry(entry)
            zipFile.getInputStream(entry).copyTo(multiOutputStream)

            // fake, used to add remaining bytes
            fakeOutputStream.closeEntry()
            // real
            zipOutputStream.closeEntry()

            bias += padding
        }

        zipFile.close()
        zipOutputStream.close()
    }
}