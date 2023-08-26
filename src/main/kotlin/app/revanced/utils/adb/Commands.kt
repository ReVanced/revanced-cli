package app.revanced.utils.adb

import se.vidstige.jadb.JadbDevice
import se.vidstige.jadb.RemoteFile
import se.vidstige.jadb.ShellProcessBuilder
import java.io.File


internal fun JadbDevice.buildCommand(command: String, su: Boolean = true): ShellProcessBuilder {
    if (su) return shellProcessBuilder("su -c \'$command\'")

    val args = command.split(" ") as ArrayList<String>
    val cmd = args.removeFirst()

    return shellProcessBuilder(cmd, *args.toTypedArray())
}

internal fun JadbDevice.run(command: String, su: Boolean = true): Int {
    return this.buildCommand(command, su).start().waitFor()
}

internal fun JadbDevice.hasSu() =
    this.startCommand("su -h", false).waitFor() == 0

internal fun JadbDevice.push(file: File, targetFilePath: String) =
    push(file, RemoteFile(targetFilePath))

internal fun JadbDevice.createFile(targetFile: String, content: String) =
    push(content.byteInputStream(), System.currentTimeMillis(), 644, RemoteFile(targetFile))


private fun JadbDevice.startCommand(command: String, su: Boolean) =
    shellProcessBuilder(if (su) "su -c '$command'" else command).start()