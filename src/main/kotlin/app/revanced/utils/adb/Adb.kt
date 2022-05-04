package app.revanced.utils.adb

import se.vidstige.jadb.JadbConnection
import se.vidstige.jadb.JadbDevice
import java.io.File
import java.util.concurrent.Executors

internal class Adb(
    private val apk: File,
    private val packageName: String,
    deviceName: String,
    private val logging: Boolean = true
) {
    private val device: JadbDevice

    init {
        device = JadbConnection().devices.find { it.serial == deviceName }
            ?: throw IllegalArgumentException("No such device with name $deviceName")

        if (device.run("su -h") == 0)
            throw IllegalArgumentException("Root required on $deviceName. Deploying failed.")
    }

    internal fun deploy() {
        // create revanced path
        device.run(Constants.COMMAND_CREATE_DIR + Constants.PATH_DATA)

        // create mount script
        device.createFile(
            Constants.PATH_INIT_PUSH,
            Constants.CONTENT_MOUNT_SCRIPT.replace(Constants.PLACEHOLDER, packageName)
        )

        // move the mount script to the revanced path
        device.run(Constants.COMMAND_MOVE_MOUNT)
        // make the mount script executable
        device.run(Constants.COMMAND_CHMOD_MOUNT + Constants.NAME_MOUNT_SCRIPT)

        // push patched file
        device.copy(Constants.PATH_INIT_PUSH, apk)
        // move patched file to revanced path
        device.run(Constants.COMMAND_MOVE_BASE)

        // kill, mount & run app
        device.run(Constants.COMMAND_KILL_APP.replace(Constants.PLACEHOLDER, packageName))
        device.run(Constants.COMMAND_MOUNT)
        device.run(Constants.COMMAND_RUN_APP.replace(Constants.PLACEHOLDER, packageName))

        // log the app
        log()

        // unmount it, after it closes
        device.run(Constants.COMMAND_UNMOUNT.replace(Constants.PLACEHOLDER, packageName))
    }

    private fun log() {
        val executor = Executors.newSingleThreadExecutor()
        val pipe = if (logging) {
            ProcessBuilder.Redirect.INHERIT
        } else {
            ProcessBuilder.Redirect.PIPE
        }

        val process = device.buildCommand(Constants.COMMAND_LOGCAT.replace(Constants.PLACEHOLDER, packageName))
            .redirectOutput(pipe)
            .redirectError(pipe)
            .useExecutor(executor)
            .start()

        Thread.sleep(250) // give the app some time to start up.
        while (true) {
            try {
                while (device.run(Constants.COMMAND_PID_OF + packageName) == 0) {
                    Thread.sleep(1000)
                }
                break
            } catch (e: Exception) {
                throw RuntimeException("An error occurred while monitoring state of app", e)
            }
        }
        println("App closed, continuing.")
        process.destroy()
        executor.shutdown()
    }
}