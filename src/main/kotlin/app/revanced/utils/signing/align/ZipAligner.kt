package app.revanced.utils.signing.align

import app.revanced.utils.signing.align.zip.structures.ZipEntry

internal object ZipAligner {
    private const val DEFAULT_ALIGNMENT = 4
    private const val LIBRARY_ALIGNMENT = 4096

    fun getEntryAlignment(entry: ZipEntry): Int? =
        if (entry.compression.toUInt() != 0u) null else if (entry.fileName.endsWith(".so")) LIBRARY_ALIGNMENT else DEFAULT_ALIGNMENT
}
