package app.revanced.utils.adb

import app.revanced.cli.logging.CliLogger
import app.revanced.utils.adb.AdbManager.Apk
import app.revanced.utils.adb.Constants.COMMAND_CREATE_DIR
import app.revanced.utils.adb.Constants.COMMAND_DELETE
import app.revanced.utils.adb.Constants.COMMAND_INSTALL_MOUNT
import app.revanced.utils.adb.Constants.COMMAND_PREPARE_MOUNT_APK
import app.revanced.utils.adb.Constants.COMMAND_RESTART
import app.revanced.utils.adb.Constants.COMMAND_UMOUNT
import app.revanced.utils.adb.Constants.CONTENT_MOUNT_SCRIPT
import app.revanced.utils.adb.Constants.PATH_INIT_PUSH
import app.revanced.utils.adb.Constants.PATH_INSTALLATION
import app.revanced.utils.adb.Constants.PATH_MOUNT
import app.revanced.utils.adb.Constants.PATH_PATCHED_APK
import app.revanced.utils.adb.Constants.PLACEHOLDER
import se.vidstige.jadb.JadbConnection
import se.vidstige.jadb.managers.Package
import se.vidstige.jadb.managers.PackageManager
import java.io.Closeable
import java.io.File

/**
 * Adb manager. Used to install and uninstall [Apk] files.
 *
 * @param deviceSerial The serial of the device.
 */
internal sealed class AdbManager(deviceSerial: String? = null, protected val logger: CliLogger? = null) : Closeable {
    protected val device = JadbConnection().devices.find { device -> device.serial == deviceSerial }
        ?: throw DeviceNotFoundException(deviceSerial)

    init {
        logger?.trace("Established connection to $deviceSerial")
    }

    /**
     * Installs the [Apk] file.
     *
     * @param apk The [Apk] file.
     */
    open fun install(apk: Apk) {
        logger?.info("Finished installing ${apk.file.name}")
    }

    /**
     * Uninstalls the package.
     *
     * @param packageName The package name.
     */
    open fun uninstall(packageName: String) {
        logger?.info("Finished uninstalling $packageName")
    }

    /**
     * Closes the [AdbManager] instance.
     */
    override fun close() {
        logger?.trace("Closed")
    }

    class RootAdbManager(deviceSerial: String, logger: CliLogger? = null) : AdbManager(deviceSerial, logger) {
        init {
            if (!device.hasSu()) throw IllegalArgumentException("Root required on $deviceSerial. Task failed")
        }

        override fun install(apk: Apk) {
            logger?.info("Installing by mounting")

            val applyReplacement = getPlaceholderReplacement(
                apk.packageName ?: throw IllegalArgumentException("Package name is required")
            )

            device.copyFile(apk.file, PATH_INIT_PUSH)

            device.run("$COMMAND_CREATE_DIR $PATH_INSTALLATION")
            device.run(COMMAND_PREPARE_MOUNT_APK.applyReplacement())

            device.createFile(PATH_INIT_PUSH, CONTENT_MOUNT_SCRIPT.applyReplacement())

            device.run(COMMAND_INSTALL_MOUNT.applyReplacement())
            device.run(COMMAND_UMOUNT.applyReplacement()) // Sanity check.
            device.run(PATH_MOUNT.applyReplacement())
            device.run(COMMAND_RESTART.applyReplacement())

            super.install(apk)
        }

        override fun uninstall(packageName: String) {
            logger?.info("Uninstalling $packageName by unmounting and deleting the package")

            val applyReplacement = getPlaceholderReplacement(packageName)

            device.run(COMMAND_UMOUNT.applyReplacement(packageName))
            device.run(COMMAND_DELETE.applyReplacement(PATH_PATCHED_APK).applyReplacement())
            device.run(COMMAND_DELETE.applyReplacement(PATH_MOUNT).applyReplacement())

            super.uninstall(packageName)
        }

        companion object Utils {
            private fun getPlaceholderReplacement(with: String): String.() -> String = { replace(PLACEHOLDER, with) }
            private fun String.applyReplacement(with: String) = replace(PLACEHOLDER, with)
        }
    }

    class UserAdbManager(deviceSerial: String, logger: CliLogger? = null) : AdbManager(deviceSerial, logger) {
        private val packageManager = PackageManager(device)

        override fun install(apk: Apk) {
            PackageManager(device).install(apk.file)

            super.install(apk)
        }

        override fun uninstall(packageName: String) {
            logger?.info("Uninstalling $packageName")

            packageManager.uninstall(Package(packageName))

            super.uninstall(packageName)
        }
    }

    /**
     * Apk file for [AdbManager].
     *
     * @param file The [Apk] file.
     */
    internal class Apk(val file: File, val packageName: String? = null)

    internal class DeviceNotFoundException(deviceSerial: String?) :
        Exception(deviceSerial?.let {
            "The device with the ADB device serial \"$deviceSerial\" can not be found"
        } ?: "No ADB device found")
}