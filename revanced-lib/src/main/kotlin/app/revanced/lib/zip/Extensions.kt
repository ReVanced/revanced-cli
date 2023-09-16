package app.revanced.lib.zip

import java.io.DataInput
import java.io.DataOutput
import java.nio.ByteBuffer

internal fun UInt.toLittleEndian() =
    (((this.toInt() and 0xff000000.toInt()) shr 24) or ((this.toInt() and 0x00ff0000) shr 8) or ((this.toInt() and 0x0000ff00) shl 8) or (this.toInt() shl 24)).toUInt()

internal fun UShort.toLittleEndian() = (this.toUInt() shl 16).toLittleEndian().toUShort()

internal fun UInt.toBigEndian() = (((this.toInt() and 0xff) shl 24) or ((this.toInt() and 0xff00) shl 8)
        or ((this.toInt() and 0x00ff0000) ushr 8) or (this.toInt() ushr 24)).toUInt()

internal fun UShort.toBigEndian() = (this.toUInt() shl 16).toBigEndian().toUShort()

internal fun ByteBuffer.getUShort() = this.getShort().toUShort()
internal fun ByteBuffer.getUInt() = this.getInt().toUInt()

internal fun ByteBuffer.putUShort(ushort: UShort) = this.putShort(ushort.toShort())
internal fun ByteBuffer.putUInt(uint: UInt) = this.putInt(uint.toInt())

internal fun DataInput.readUShort() = this.readShort().toUShort()
internal fun DataInput.readUInt() = this.readInt().toUInt()

internal fun DataOutput.writeUShort(ushort: UShort) = this.writeShort(ushort.toInt())
internal fun DataOutput.writeUInt(uint: UInt) = this.writeInt(uint.toInt())

internal fun DataInput.readUShortLE() = this.readUShort().toBigEndian()
internal fun DataInput.readUIntLE() = this.readUInt().toBigEndian()

internal fun DataOutput.writeUShortLE(ushort: UShort) = this.writeUShort(ushort.toLittleEndian())
internal fun DataOutput.writeUIntLE(uint: UInt) = this.writeUInt(uint.toLittleEndian())
