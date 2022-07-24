package app.revanced.utils.signing.align

import app.revanced.utils.signing.align.zip.ZipFile
import java.io.File

internal object ZipAligner {
    private const val DEFAULT_ALIGNMENT = 4
    private const val LIBRARY_ALIGNMENT = 4096

    fun align(input: File, output: File) {
        val inputZip = ZipFile(input)
        val outputZip = ZipFile(output)

        for (entry in inputZip.entries) {
            val data = inputZip.getDataForEntry(entry)

            if (entry.compression == 0.toUShort()) {
                val alignment = if (entry.fileName.endsWith(".so")) LIBRARY_ALIGNMENT else DEFAULT_ALIGNMENT

                outputZip.addEntryAligned(entry, data, alignment)
            } else {
                outputZip.addEntry(entry, data)
            }
        }

        outputZip.finish()
    }
}
