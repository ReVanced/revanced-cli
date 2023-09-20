package app.revanced.lib.adb

import app.revanced.lib.adb.AdbManager.Apk
import app.revanced.lib.adb.Constants.CREATE_DIR
import app.revanced.lib.adb.Constants.DELETE
import app.revanced.lib.adb.Constants.INSTALLATION_PATH
import app.revanced.lib.adb.Constants.INSTALL_MOUNT
import app.revanced.lib.adb.Constants.INSTALL_PATCHED_APK
import app.revanced.lib.adb.Constants.MOUNT_PATH
import app.revanced.lib.adb.Constants.MOUNT_SCRIPT
import app.revanced.lib.adb.Constants.PATCHED_APK_PATH
import app.revanced.lib.adb.Constants.PLACEHOLDER
import app.revanced.lib.adb.Constants.RESOLVE_ACTIVITY
import app.revanced.lib.adb.Constants.RESTART
import app.revanced.lib.adb.Constants.TMP_PATH
import app.revanced.lib.adb.Constants.UMOUNT
import se.vidstige.jadb.JadbConnection
import se.vidstige.jadb.JadbDevice
import se.vidstige.jadb.managers.Package
import se.vidstige.jadb.managers.PackageManager
import java.io.File
import java.util.logging.Logger

/**
 * Adb manager. Used to install and uninstall [Apk] files.
 *
 * @param deviceSerial The serial of the device.
 */
sealed class AdbManager private constructor(deviceSerial: String? = null) {
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

    companion object {
        /**
         * Gets an [AdbManager] for the supplied device serial.
         *
         * @param deviceSerial The device serial.
         * @param root Whether to use root or not.
         * @return The [AdbManager].
         * @throws DeviceNotFoundException If the device can not be found.
         */
        fun getAdbManager(deviceSerial: String, root: Boolean = false): AdbManager =
            if (root) RootAdbManager(deviceSerial) else UserAdbManager(deviceSerial)
    }

    class RootAdbManager internal constructor(deviceSerial: String) : AdbManager(deviceSerial) {
        init {
            if (!device.hasSu()) throw IllegalArgumentException("Root required on $deviceSerial. Task failed")
        }

        override fun install(apk: Apk) {
            logger.info("Installing by mounting")

            val packageName = apk.packageName ?: throw PackageNameRequiredException()

            device.run(RESOLVE_ACTIVITY, packageName).inputStream.bufferedReader().readLine().let { line ->
                if (line != "No activity found") return@let
                throw throw FailedToFindInstalledPackageException(packageName)
            }

            device.push(apk.file, TMP_PATH)

            device.run("$CREATE_DIR $INSTALLATION_PATH")
            device.run(INSTALL_PATCHED_APK, packageName)

            device.createFile(TMP_PATH, MOUNT_SCRIPT.applyReplacement(packageName))

            device.run(INSTALL_MOUNT, packageName)
            device.run(UMOUNT, packageName) // Sanity check.
            device.run(MOUNT_PATH, packageName)
            device.run(RESTART, packageName)
            device.run(DELETE, TMP_PATH)

            super.install(apk)
        }

        override fun uninstall(packageName: String) {
            logger.info("Uninstalling $packageName by unmounting")

            device.run(UMOUNT, packageName)
            device.run(DELETE.applyReplacement(PATCHED_APK_PATH), packageName)
            device.run(DELETE, MOUNT_PATH)
            device.run(DELETE, TMP_PATH)

            super.uninstall(packageName)
        }

        companion object Utils {
            private fun JadbDevice.run(command: String, with: String) = run(command.applyReplacement(with))
            private fun String.applyReplacement(with: String) = replace(PLACEHOLDER, with)
        }
    }

    class UserAdbManager internal constructor(deviceSerial: String) : AdbManager(deviceSerial) {
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
    class Apk(val file: File, val packageName: String? = null)

    class DeviceNotFoundException internal constructor(deviceSerial: String?) :
        Exception(deviceSerial?.let {
            "The device with the ADB device serial \"$deviceSerial\" can not be found"
        } ?: "No ADB device found")

    class FailedToFindInstalledPackageException internal constructor(packageName: String) :
        Exception("Failed to find installed package \"$packageName\" because no activity was found")

    class PackageNameRequiredException internal constructor() :
        Exception("Package name is required")
}