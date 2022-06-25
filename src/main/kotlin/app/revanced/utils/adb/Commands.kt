package app.revanced.utils.adb

import app.revanced.cli.command.MainCommand.logger
import se.vidstige.jadb.JadbDevice
import se.vidstige.jadb.RemoteFile
import se.vidstige.jadb.ShellProcessBuilder
import java.io.File

internal fun JadbDevice.buildCommand(command: String, su: Boolean = true): ShellProcessBuilder {
    if (su) {
        return shellProcessBuilder("su -c \'$command\'")
    }

    val args = command.split(" ") as ArrayList<String>
    val cmd = args.removeFirst()

    return shellProcessBuilder(cmd, *args.toTypedArray())
}

internal fun JadbDevice.run(command: String, su: Boolean = true): Int {
    if (su) {
        return this.buildCommand(command, true).start().waitFor()
    }

    // Avoid deadlock whilst checking for root access
    val byteArray = ByteArray(8) // Length of 'MagiskSU'
    val adbCommand = this.buildCommand(command, false).start()
    val adbInputStream = adbCommand.inputStream
    adbInputStream.read(byteArray)

    // Check if SuperSU or some other unsupported SU
    if (String(byteArray).filter { !it.isWhitespace() } == "SuperSU") {
        Constants.SUPERSU = true
    }
    else if (String(byteArray).filter { !it.isWhitespace() }.contains("SU") && String(byteArray) != "MagiskSU") {
        logger.warn("Unsupported SU.")
    }

    adbInputStream.close()
    return adbCommand.waitFor()
}

internal fun JadbDevice.copy(targetPath: String, file: File) {
    push(file, RemoteFile(targetPath))
}

internal fun JadbDevice.createFile(targetFile: String, content: String) {
    push(content.byteInputStream(), System.currentTimeMillis(), 644, RemoteFile(targetFile))
}