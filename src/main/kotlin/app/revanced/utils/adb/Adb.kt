package app.revanced.utils.adb

import app.revanced.cli.command.MainCommand.logger
import se.vidstige.jadb.JadbConnection
import se.vidstige.jadb.JadbDevice
import se.vidstige.jadb.managers.PackageManager
import java.io.File
import java.util.concurrent.Executors

internal class Adb(
    private val file: File? = null,
    private var packageName: String,
    deviceName: String,
    private val modeInstall: Boolean = false,
    private val logging: Boolean = true
) {
    constructor(deviceName: String): this(
        null,
        Constants.PLACEHOLDER,
        deviceName,
        false
    )

    private val device: JadbDevice

    init {
        device = JadbConnection().devices.find { it.serial == deviceName }
            ?: throw IllegalArgumentException("No such device with name $deviceName")

        if (!modeInstall && device.run("su -h", false) != 0)
            throw IllegalArgumentException("Root required on $deviceName. Task failed")
    }

    private fun String.replacePlaceholder(with: String? = null): String {
        return this.replace(Constants.PLACEHOLDER, with ?: packageName)
    }

    internal fun deploy() {
        if (modeInstall) {
            logger.info("Installing without mounting")

            PackageManager(device).install(file)
        } else {
            logger.info("Installing by mounting")

            // push patched file
            device.copy(Constants.PATH_INIT_PUSH, file!!)

            // create revanced folder path
            device.run("${Constants.COMMAND_CREATE_DIR} ${Constants.PATH_REVANCED}")

            // prepare mounting the apk
            device.run(Constants.COMMAND_PREPARE_MOUNT_APK.replacePlaceholder())

            // push mount script
            device.createFile(
                Constants.PATH_INIT_PUSH,
                Constants.CONTENT_MOUNT_SCRIPT.replacePlaceholder()
            )
            // install mount script
            device.run(Constants.COMMAND_INSTALL_MOUNT.replacePlaceholder())

            // unmount the apk for sanity
            device.run(Constants.COMMAND_UMOUNT.replacePlaceholder())
            // mount the apk
            device.run(Constants.PATH_MOUNT.replacePlaceholder())

            // relaunch app
            device.run(Constants.COMMAND_RESTART.replacePlaceholder())

            // log the app
            log()
        }
    }

    internal fun uninstall() {
        val adbLookupFiles = device.buildCommand("ls ${Constants.PATH_REVANCED}").start()
        val inputStreamReader = adbLookupFiles.inputReader()

        val fileList = inputStreamReader.readLines()
        if (fileList.isEmpty()) {
            logger.error("No mounted apps found")
            return
        }
        fileList.forEachIndexed { index, file ->
            println("$index: $file")
        }

        println("Which app do you want to uninstall?")
        val fileToUninstall = readln().toInt()
        packageName = File(fileList[fileToUninstall]).nameWithoutExtension

        logger.info("Uninstalling $packageName by unmounting")
        // unmount the apk
        device.run(Constants.COMMAND_UMOUNT.replacePlaceholder())

        // delete revanced app
        device.delete(Constants.PATH_REVANCED_APP.replacePlaceholder())

        // delete mount script
        device.delete(Constants.PATH_MOUNT.replacePlaceholder())

        logger.info("Finished uninstalling")
    }

    private fun log() {
        val executor = Executors.newSingleThreadExecutor()
        val pipe = if (logging) {
            ProcessBuilder.Redirect.INHERIT
        } else {
            ProcessBuilder.Redirect.PIPE
        }

        val process = device.buildCommand(Constants.COMMAND_LOGCAT.replacePlaceholder())
            .redirectOutput(pipe)
            .redirectError(pipe)
            .useExecutor(executor)
            .start()

        Thread.sleep(500) // give the app some time to start up.
        while (true) {
            try {
                while (device.run("${Constants.COMMAND_PID_OF} $packageName") == 0) {
                    Thread.sleep(1000)
                }
                break
            } catch (e: Exception) {
                throw RuntimeException("An error occurred while monitoring the state of app", e)
            }
        }
        logger.info("Stopped logging because the app was closed")
        process.destroy()
        executor.shutdown()
    }
}
