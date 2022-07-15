package app.revanced.utils.signing.align

import app.revanced.utils.signing.align.zip.ZipFile
import java.io.File
import java.util.*

internal object ZipAligner {
    const val DEFAULT_ALIGNMENT = 4
    const val LIBRARY_ALIGNEMNT = 4096

    fun align(input: File, output: File) {
        val inputZip = ZipFile(input)
        val outputZip = ZipFile(output)

        for (entry in inputZip.entries) {
            val data = inputZip.getDataForEntry(entry)

            if (entry.compression == 0.toUShort()) {
                val alignment = if (entry.fileName.endsWith(".so")) LIBRARY_ALIGNEMNT else DEFAULT_ALIGNMENT

                outputZip.addEntryAligned(entry, data, alignment)
            } else {
                outputZip.addEntry(entry, data)
            }
        }

        outputZip.finish()
    }
}
