package app.revanced.cli.runner

import app.revanced.cli.utils.DexReplacer
import app.revanced.cli.utils.Scripts
import app.revanced.cli.utils.signer.Signer
import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarBuilder
import me.tongfei.progressbar.ProgressBarStyle
import org.jf.dexlib2.writer.io.MemoryDataStore
import se.vidstige.jadb.JadbConnection
import se.vidstige.jadb.JadbDevice
import se.vidstige.jadb.RemoteFile
import se.vidstige.jadb.ShellProcessBuilder
import java.io.File
import java.nio.file.Files
import java.util.concurrent.Executors

object Emulator {
    fun emulate(
        apk: File,
        dexFiles: Map<String, MemoryDataStore>,
        deviceName: String
    ) {
        lateinit var dvc: JadbDevice
        pbar("Initializing").use { bar ->
            dvc = JadbConnection().findDevice(deviceName)
                ?: throw IllegalArgumentException("No such device with name $deviceName")
            if (!dvc.hasSu())
                throw IllegalArgumentException("Device $deviceName is not rooted or does not have su")
            bar.step()
        }

        lateinit var tmpFile: File // we need this file at the end to clean up.
        pbar("Generating APK file", 3).use { bar ->
            bar.step().extraMessage = "Creating APK file"
            tmpFile = Files.createTempFile("rvc-cli", ".apk").toFile()
            apk.copyTo(tmpFile, true)

            bar.step().extraMessage = "Replacing dex files"
            DexReplacer.replaceDex(tmpFile, dexFiles)

            bar.step().extraMessage = "Signing APK file"
            Signer.signApk(tmpFile)
        }

        pbar("Running application", 6, false).use { bar ->
            bar.step().extraMessage = "Pushing mount scripts"
            dvc.push(Scripts.MOUNT_SCRIPT, RemoteFile(Scripts.SCRIPT_PATH))
            dvc.cmd(Scripts.CREATE_DIR_COMMAND).assertZero()
            dvc.cmd(Scripts.MV_MOUNT_COMMAND).assertZero()
            dvc.cmd(Scripts.CHMOD_MOUNT_COMMAND).assertZero()

            bar.step().extraMessage = "Pushing APK file"
            dvc.push(tmpFile, RemoteFile(Scripts.APK_PATH))

            bar.step().extraMessage = "Mounting APK file"
            dvc.cmd(Scripts.STOP_APP_COMMAND).startAndWait()
            dvc.cmd(Scripts.START_MOUNT_COMMAND).assertZero()

            bar.step().extraMessage = "Starting APK file"
            dvc.cmd(Scripts.START_APP_COMMAND).assertZero()

            bar.step().setExtraMessage("Debugging APK file").refresh()
            println("\nWaiting until app is closed.")
            val executor = Executors.newSingleThreadExecutor()
            val p = dvc.cmd(Scripts.LOGCAT_COMMAND)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .useExecutor(executor)
                .start()
            Thread.sleep(250) // give the app some time to start up.
            while (dvc.cmd(Scripts.PIDOF_APP_COMMAND).startAndWait() == 0) {
                Thread.sleep(250)
            }
            println("App closed, continuing.")
            p.destroy()
            executor.shutdown()

            bar.step().extraMessage = "Unmounting APK file"
            var exitCode: Int
            do {
                exitCode = dvc.cmd(Scripts.UNMOUNT_COMMAND).startAndWait()
            } while (exitCode != 0)
        }

        tmpFile.delete()
    }
}

private fun JadbDevice.push(s: String, remoteFile: RemoteFile) =
    this.push(s.byteInputStream(), System.currentTimeMillis(), 644, remoteFile)

private fun JadbConnection.findDevice(device: String): JadbDevice? {
    return devices.find { it.serial == device }
}

private fun JadbDevice.cmd(s: String): ShellProcessBuilder {
    val args = s.split(" ") as ArrayList<String>
    val cmd = args.removeFirst()
    return shellProcessBuilder(cmd, *args.toTypedArray())
}

private fun JadbDevice.hasSu(): Boolean {
    return cmd("su -h").startAndWait() == 0
}

private fun ShellProcessBuilder.startAndWait(): Int {
    return start().waitFor()
}

private fun ShellProcessBuilder.assertZero() {
    if (startAndWait() != 0) {
        val cmd = getcmd()
        throw IllegalStateException("ADB returned non-zero status code for command: $cmd")
    }
}

private fun pbar(task: String, steps: Long = 1, update: Boolean = true): ProgressBar {
    val b = ProgressBarBuilder().setTaskName(task)
    if (update) b
        .setUpdateIntervalMillis(250)
        .continuousUpdate()
    return b
        .setStyle(ProgressBarStyle.ASCII)
        .build()
        .maxHint(steps + 1)
}

private fun ProgressBar.use(block: (ProgressBar) -> Unit) {
    block(this)
    stepTo(max) // step to 100%
    extraMessage = "" // clear extra message
    close()
}

private fun ShellProcessBuilder.getcmd(): String {
    val f = this::class.java.getDeclaredField("command")
    f.isAccessible = true
    return f.get(this) as String
}