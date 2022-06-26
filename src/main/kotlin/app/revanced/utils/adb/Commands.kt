package app.revanced.utils.adb

import app.revanced.cli.command.MainCommand.logger
import se.vidstige.jadb.JadbDevice
import se.vidstige.jadb.RemoteFile
import se.vidstige.jadb.ShellProcess
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
        return this.buildCommand(command).start().waitFor()
    }
    
    return CheckSU(command)!!.waitFor()
}

private fun JadbDevice.CheckSU(command: String): ShellProcess? {
    val byteArray = ByteArray(8) // Length of 'MagiskSU'
    val adbCommand = this.buildCommand(command, false).start()

    // fix: deadlock with SuperSU
    val adbInputStream = adbCommand.inputStream
    adbInputStream.read(byteArray)

    if (String(byteArray).filter { !it.isWhitespace() } == "SuperSU") {
        Constants.SUPERSU = true
    } else if (String(byteArray).filter { !it.isWhitespace() }.contains("SU") && String(byteArray) != "MagiskSU") {
        logger.warn("Unsupported SU, this process may fail...")
    }
    adbInputStream.close()
    return adbCommand
}

internal fun JadbDevice.copy(targetPath: String, file: File) {
    push(file, RemoteFile(targetPath))
}

internal fun JadbDevice.createFile(targetFile: String, content: String) {
    push(content.byteInputStream(), System.currentTimeMillis(), 644, RemoteFile(targetFile))
}