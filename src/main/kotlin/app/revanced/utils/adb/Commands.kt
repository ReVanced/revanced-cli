package app.revanced.utils.adb

import se.vidstige.jadb.JadbDevice
import se.vidstige.jadb.RemoteFile
import se.vidstige.jadb.ShellProcess
import se.vidstige.jadb.ShellProcessBuilder
import java.io.File

internal fun JadbDevice.buildCommand(command: String, su: Boolean = true): ShellProcessBuilder {
    if (su) {
        return shellProcessBuilder("su -mm -c \'$command\'")
    }

    val args = command.split(" ") as ArrayList<String>
    val cmd = args.removeFirst()

    return shellProcessBuilder(cmd, *args.toTypedArray())
}

internal fun JadbDevice.run(command: String, su: Boolean = true): Int {
    if (su) {
        return this.buildCommand(command).start().waitFor()
    }
    
    return this.CheckSU(command)!!.waitFor()
}

private fun JadbDevice.CheckSU(command: String): ShellProcess? {
    val byteArray = ByteArray(8)
    val adbCommand = this.buildCommand(command, false).start()

    // fix: deadlock with SuperSU
    val adbInputStream = adbCommand.inputStream
    adbInputStream.read(byteArray)

    val SuperUserType = String(byteArray).filter { !it.isWhitespace() }
    if (SuperUserType == "SuperSU") {
        Constants.IS_SUPERSU = true
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
