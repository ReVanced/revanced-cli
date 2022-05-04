package app.revanced.utils.adb

import se.vidstige.jadb.JadbDevice
import se.vidstige.jadb.RemoteFile
import se.vidstige.jadb.ShellProcessBuilder
import java.io.File

internal fun JadbDevice.buildCommand(command: String, su: Boolean = true): ShellProcessBuilder {
    val args = command.split(" ") as ArrayList<String>
    val cmd = args.removeFirst()

    return shellProcessBuilder(cmd, *args.toTypedArray())
}

internal fun JadbDevice.run(command: String, su: Boolean = true): Int {
    return this.buildCommand(command).start().waitFor()
}

internal fun JadbDevice.copy(targetPath: String, file: File) {
    push(file, RemoteFile(targetPath))
}

internal fun JadbDevice.createFile(targetFile: String, content: String, su: Boolean = true) {
    push(content.byteInputStream(), System.currentTimeMillis(), 644, RemoteFile(targetFile))
}