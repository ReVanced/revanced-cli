package app.revanced.utils.adb

import se.vidstige.jadb.JadbDevice
import se.vidstige.jadb.RemoteFile
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors

// return the input or output stream, depending on which first returns a value
internal fun JadbDevice.run(command: String, su: Boolean = false) = with(this.startCommand(command, su)) {
    Executors.newFixedThreadPool(2).let { service ->
        arrayOf(inputStream, errorStream).map { stream ->
            Callable { stream.bufferedReader().use { it.readLine() } }
        }.let { tasks -> service.invokeAny(tasks).also { service.shutdown() } }
    }
}

internal fun JadbDevice.hasSu() =
    this.startCommand("su -h", false).waitFor() == 0

internal fun JadbDevice.copyFile(file: File, targetFile: String) =
    push(file, RemoteFile(targetFile))

internal fun JadbDevice.createFile(targetFile: String, content: String) =
    push(content.byteInputStream(), System.currentTimeMillis(), 644, RemoteFile(targetFile))


private fun JadbDevice.startCommand(command: String, su: Boolean) =
    shellProcessBuilder(if (su) "su -c '$command'" else command).start()