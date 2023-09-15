package app.revanced.utils.align.zip

import app.revanced.utils.align.zip.structures.ZipEndRecord
import app.revanced.utils.align.zip.structures.ZipEntry
import java.io.Closeable
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.util.zip.CRC32
import java.util.zip.Deflater

class ZipFile(file: File) : Closeable {
    private var entries: MutableList<ZipEntry> = mutableListOf()

    private val filePointer: RandomAccessFile = RandomAccessFile(file, "rw")
    private var centralDirectoryNeedsRewrite = false

    private val compressionLevel = 5

    init {
        // If file isn't empty try to load entries.
        if (file.length() > 0) {
            val endRecord = findEndRecord()

            if (endRecord.diskNumber > 0u || endRecord.totalEntries != endRecord.diskEntries)
                throw IllegalArgumentException("Multi-file archives are not supported")

            entries = readEntries(endRecord).toMutableList()
        }

        // Seek back to start for writing.
        filePointer.seek(0)
    }

    private fun findEndRecord(): ZipEndRecord {
        // Look from end to start since end record is at the end.
        for (i in filePointer.length() - 1 downTo 0) {
            filePointer.seek(i)
            // Possible beginning of signature.
            if (filePointer.readByte() == 0x50.toByte()) {
                // Seek back to get the full int.
                filePointer.seek(i)
                val possibleSignature = filePointer.readUIntLE()
                if (possibleSignature == ZipEndRecord.ECD_SIGNATURE) {
                    filePointer.seek(i)
                    return ZipEndRecord.fromECD(filePointer)
                }
            }
        }

        throw Exception("Couldn't find end record")
    }

    private fun readEntries(endRecord: ZipEndRecord): List<ZipEntry> {
        filePointer.seek(endRecord.centralDirectoryStartOffset.toLong())

        val numberOfEntries = endRecord.diskEntries.toInt()

        return buildList(numberOfEntries) {
            for (i in 1..numberOfEntries) {
                add(
                    ZipEntry.fromCDE(filePointer).also
                    {
                        //for some reason the local extra field can be different from the central one
                        it.readLocalExtra(
                            filePointer.channel.map(
                                FileChannel.MapMode.READ_ONLY,
                                it.localHeaderOffset.toLong() + 28,
                                2
                            )
                        )
                    })
            }
        }
    }

    private fun writeCD() {
        val centralDirectoryStartOffset = filePointer.channel.position().toUInt()

        entries.forEach {
            filePointer.channel.write(it.toCDE())
        }

        val entriesCount = entries.size.toUShort()

        val endRecord = ZipEndRecord(
            0u,
            0u,
            entriesCount,
            entriesCount,
            filePointer.channel.position().toUInt() - centralDirectoryStartOffset,
            centralDirectoryStartOffset,
            ""
        )

        filePointer.channel.write(endRecord.toECD())
    }

    private fun addEntry(entry: ZipEntry, data: ByteBuffer) {
        centralDirectoryNeedsRewrite = true

        entry.localHeaderOffset = filePointer.channel.position().toUInt()

        filePointer.channel.write(entry.toLFH())
        filePointer.channel.write(data)

        entries.add(entry)
    }

    fun addEntryCompressData(entry: ZipEntry, data: ByteArray) {
        val compressor = Deflater(compressionLevel, true)
        compressor.setInput(data)
        compressor.finish()

        val uncompressedSize = data.size
        val compressedData = ByteArray(uncompressedSize) // I'm guessing compression won't make the data bigger.

        val compressedDataLength = compressor.deflate(compressedData)
        val compressedBuffer =
            ByteBuffer.wrap(compressedData.take(compressedDataLength).toByteArray())

        compressor.end()

        val crc = CRC32()
        crc.update(data)

        entry.compression = 8u // Deflate compression.
        entry.uncompressedSize = uncompressedSize.toUInt()
        entry.compressedSize = compressedDataLength.toUInt()
        entry.crc32 = crc.value.toUInt()

        addEntry(entry, compressedBuffer)
    }

    private fun addEntryCopyData(entry: ZipEntry, data: ByteBuffer, alignment: Int? = null) {
        alignment?.let {
            // Calculate where data would end up.
            val dataOffset = filePointer.filePointer + entry.LFHSize

            val mod = dataOffset % alignment

            // Wrong alignment.
            if (mod != 0L) {
                // Add padding at end of extra field.
                entry.localExtraField =
                    entry.localExtraField.copyOf((entry.localExtraField.size + (alignment - mod)).toInt())
            }
        }

        addEntry(entry, data)
    }

    private fun getDataForEntry(entry: ZipEntry): ByteBuffer {
        return filePointer.channel.map(
            FileChannel.MapMode.READ_ONLY,
            entry.dataOffset.toLong(),
            entry.compressedSize.toLong()
        )
    }

    /**
     * Copies all entries from [file] to this file but skip already existing entries.
     *
     * @param file The file to copy entries from.
     * @param entryAlignment A function that returns the alignment for a given entry.
     */
    fun copyEntriesFromFileAligned(file: ZipFile, entryAlignment: (entry: ZipEntry) -> Int?) {
        for (entry in file.entries) {
            if (entries.any { it.fileName == entry.fileName }) continue // Skip duplicates

            val data = file.getDataForEntry(entry)
            addEntryCopyData(entry, data, entryAlignment(entry))
        }
    }

    override fun close() {
        if (centralDirectoryNeedsRewrite) writeCD()
        filePointer.close()
    }
}