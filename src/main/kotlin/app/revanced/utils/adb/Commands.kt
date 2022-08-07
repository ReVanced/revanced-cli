package app.revanced.utils.adb

import se.vidstige.jadb.JadbDevice
import se.vidstige.jadb.RemoteFile
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
    return this.buildCommand(command, su).start().waitFor()
}

internal fun JadbDevice.isRooted(): Boolean {
    val adbCommand = this.buildCommand("su -h", false).start()

    // Size of the buffer to read the output from the shell process
    val suTypeArray = ByteArray(8)

    val adbInputStream = adbCommand.inputStream
    adbInputStream.read(suTypeArray)

    val suType = String(suTypeArray).split(" ")[0].uppercase()
    Adb.rootType = RootType.values().find { it.name == suType } ?: RootType.NONE_OR_UNSUPPORTED

    adbInputStream.close()
    return adbCommand.waitFor() == 0
}

internal fun JadbDevice.copy(targetPath: String, file: File) {
    push(file, RemoteFile(targetPath))
}

internal fun JadbDevice.createFile(targetFile: String, content: String) {
    push(content.byteInputStream(), System.currentTimeMillis(), 644, RemoteFile(targetFile))
}