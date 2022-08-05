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

    return this.checkSU(command).waitFor()
}

private fun JadbDevice.checkSU(command: String): ShellProcess {
    // Size of the buffer to read the output from the shell process
    val suTypeArray = ByteArray(8)
    
    val adbCommand = this.buildCommand(command, false).start()
    val adbInputStream = adbCommand.inputStream
    adbInputStream.read(suTypeArray)

    val suType = String(suTypeArray).split(" ")[0].uppercase()
    Adb.rootType = RootType.values().find { it.name == suType } ?: RootType.NONE_OR_UNSUPPORTED

    adbInputStream.close()
    return adbCommand
}

internal fun JadbDevice.copy(targetPath: String, file: File) {
    push(file, RemoteFile(targetPath))
}

internal fun JadbDevice.createFile(targetFile: String, content: String) {
    push(content.byteInputStream(), System.currentTimeMillis(), 644, RemoteFile(targetFile))
}