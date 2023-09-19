package app.revanced.lib.zip

import app.revanced.lib.zip.structures.ZipEntry

object ZipAligner {
    private const val DEFAULT_ALIGNMENT = 4
    private const val LIBRARY_ALIGNMENT = 4096

    val apkZipEntryAlignment = { entry: ZipEntry ->
        if (entry.compression.toUInt() != 0u) null
        else if (entry.fileName.endsWith(".so")) LIBRARY_ALIGNMENT
        else DEFAULT_ALIGNMENT
    }
}
