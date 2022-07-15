package app.revanced.utils.signing.align.zip

import app.revanced.utils.signing.align.zip.structures.ZipEndRecord
import app.revanced.utils.signing.align.zip.structures.ZipEntry
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class ZipFile(val file: File) {
    var entries: MutableList<ZipEntry> = mutableListOf()

    private val filePointer: RandomAccessFile = RandomAccessFile(file, "rw")

    init {
        //if file isn't empty try to load entries
        if (file.length() > 0) {
            val endRecord = findEndRecord()

            if (endRecord.diskNumber > 0u || endRecord.totalEntries != endRecord.diskEntries)
                throw IllegalArgumentException("Multi-file archives are not supported")

            entries = readEntries(endRecord).toMutableList()
        }

        //seek back to start for writing
        filePointer.seek(0)
    }

    private fun findEndRecord(): ZipEndRecord {
        //look from end to start since end record is at the end
        for (i in filePointer.length() - 1 downTo 0) {
            filePointer.seek(i)
            //possible beginning of signature
            if (filePointer.readByte() == 0x50.toByte()) {
                //seek back to get the full int
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
                add(ZipEntry.fromCDE(filePointer).also
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

    private fun writeCDE() {
        val CDEStart = filePointer.channel.position().toUInt()

        entries.forEach {
            filePointer.channel.write(it.toCDE())
        }

        val endRecord = ZipEndRecord(
            0u,
            0u,
            entries.count().toUShort(),
            entries.count().toUShort(),
            filePointer.channel.position().toUInt() - CDEStart,
            CDEStart,
            ""
        )

        filePointer.channel.write(endRecord.toECD())
    }

    fun addEntry(entry: ZipEntry, data: ByteBuffer) {
        entry.localHeaderOffset = filePointer.channel.position().toUInt()

        filePointer.channel.write(entry.toLFH())
        filePointer.channel.write(data)

        entries.add(entry)
    }

    fun addEntryAligned(entry: ZipEntry, data: ByteBuffer, alignment: Int) {
        //calculate where data would end up
        val dataOffset = filePointer.filePointer + entry.LFHSize

        val mod = dataOffset % alignment

        //wrong alignment
        if (mod != 0L) {
            //add padding at end of extra field
            entry.localExtraField =
                entry.localExtraField.copyOf((entry.localExtraField.size + (alignment - mod)).toInt())
        }

        addEntry(entry, data)
    }

    fun getDataForEntry(entry: ZipEntry): ByteBuffer {
        return filePointer.channel.map(
            FileChannel.MapMode.READ_ONLY,
            entry.dataOffset.toLong(),
            entry.compressedSize.toLong()
        )
    }

    fun finish() {
        writeCDE()
        filePointer.close()
    }
}
