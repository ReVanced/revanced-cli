package app.revanced.utils.adb

import app.revanced.utils.adb.AdbManager.Apk
import app.revanced.utils.adb.Constants.CREATE_DIR
import app.revanced.utils.adb.Constants.DELETE
import app.revanced.utils.adb.Constants.INSTALLATION_PATH
import app.revanced.utils.adb.Constants.INSTALL_MOUNT
import app.revanced.utils.adb.Constants.INSTALL_PATCHED_APK
import app.revanced.utils.adb.Constants.MOUNT_PATH
import app.revanced.utils.adb.Constants.MOUNT_SCRIPT
import app.revanced.utils.adb.Constants.PATCHED_APK_PATH
import app.revanced.utils.adb.Constants.PLACEHOLDER
import app.revanced.utils.adb.Constants.RESTART
import app.revanced.utils.adb.Constants.TMP_PATH
import app.revanced.utils.adb.Constants.UMOUNT
import se.vidstige.jadb.JadbConnection
import se.vidstige.jadb.managers.Package
import se.vidstige.jadb.managers.PackageManager
import java.io.Closeable
import java.io.File
import java.util.logging.Logger

/**
 * Adb manager. Used to install and uninstall [Apk] files.
 *
 * @param deviceSerial The serial of the device.
 */
internal sealed class AdbManager(deviceSerial: String? = null) : Closeable {
    protected val logger: Logger = Logger.getLogger(AdbManager::class.java.name)

    protected val device = JadbConnection().devices.find { device -> device.serial == deviceSerial }
        ?: throw DeviceNotFoundException(deviceSerial)

    init {
        logger.fine("Established connection to $deviceSerial")
    }

    /**
     * Installs the [Apk] file.
     *
     * @param apk The [Apk] file.
     */
    open fun install(apk: Apk) {
        logger.info("Finished installing ${apk.file.name}")
    }

    /**
     * Uninstalls the package.
     *
     * @param packageName The package name.
     */
    open fun uninstall(packageName: String) {
        logger.info("Finished uninstalling $packageName")
    }

    /**
     * Closes the [AdbManager] instance.
     */
    override fun close() {
        logger.fine("Closed")
    }

    class RootAdbManager(deviceSerial: String) : AdbManager(deviceSerial) {
        init {
            if (!device.hasSu()) throw IllegalArgumentException("Root required on $deviceSerial. Task failed")
        }

        override fun install(apk: Apk) {
            logger.info("Installing by mounting")

            val applyReplacement = getPlaceholderReplacement(
                apk.packageName ?: throw IllegalArgumentException("Package name is required")
            )

            device.push(apk.file, TMP_PATH)

            device.run("$CREATE_DIR $INSTALLATION_PATH")
            device.run(INSTALL_PATCHED_APK.applyReplacement())

            device.createFile(TMP_PATH, MOUNT_SCRIPT.applyReplacement())

            device.run(INSTALL_MOUNT.applyReplacement())
            device.run(UMOUNT.applyReplacement()) // Sanity check.
            device.run(MOUNT_PATH.applyReplacement())
            device.run(RESTART.applyReplacement())
            device.run(DELETE.applyReplacement(TMP_PATH).applyReplacement())

            super.install(apk)
        }

        override fun uninstall(packageName: String) {
            logger.info("Uninstalling $packageName by unmounting")

            val applyReplacement = getPlaceholderReplacement(packageName)

            device.run(UMOUNT.applyReplacement(packageName))
            device.run(DELETE.applyReplacement(PATCHED_APK_PATH).applyReplacement())
            device.run(DELETE.applyReplacement(MOUNT_PATH).applyReplacement())
            device.run(DELETE.applyReplacement(TMP_PATH).applyReplacement())

            super.uninstall(packageName)
        }

        companion object Utils {
            private fun getPlaceholderReplacement(with: String): String.() -> String = { replace(PLACEHOLDER, with) }
            private fun String.applyReplacement(with: String) = replace(PLACEHOLDER, with)
        }
    }

    class UserAdbManager(deviceSerial: String) : AdbManager(deviceSerial) {
        private val packageManager = PackageManager(device)

        override fun install(apk: Apk) {
            PackageManager(device).install(apk.file)

            super.install(apk)
        }

        override fun uninstall(packageName: String) {
            logger.info("Uninstalling $packageName")

            packageManager.uninstall(Package(packageName))

            super.uninstall(packageName)
        }
    }

    /**
     * Apk file for [AdbManager].
     *
     * @param file The [Apk] file.
     * @param packageName The package name of the [Apk] file.
     */
    internal class Apk(val file: File, val packageName: String? = null)

    internal class DeviceNotFoundException(deviceSerial: String?) :
        Exception(deviceSerial?.let {
            "The device with the ADB device serial \"$deviceSerial\" can not be found"
        } ?: "No ADB device found")
}