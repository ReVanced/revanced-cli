package app.revanced.utils.adb

import app.revanced.cli.logging.CliLogger
import se.vidstige.jadb.JadbConnection
import se.vidstige.jadb.JadbDevice
import se.vidstige.jadb.managers.Package
import se.vidstige.jadb.managers.PackageManager
import java.io.Closeable
import java.io.File
import java.nio.file.Files

internal sealed class Adb(deviceSerial: String) : Closeable {
    protected val device: JadbDevice = JadbConnection().devices.find { it.serial == deviceSerial }
        ?: throw IllegalArgumentException("The device with the serial $deviceSerial can not be found.")

    protected val packageManager = PackageManager(device)

    open val logger: CliLogger? = null

    abstract fun install(base: Apk, splits: List<Apk>)

    abstract fun uninstall(packageName: String)

    override fun close() {
        logger?.trace("Closed")
    }

    class RootAdb(deviceSerial: String, override val logger: CliLogger? = null) : Adb(deviceSerial) {
        init {
            if (!device.hasSu()) throw IllegalArgumentException("Root required on $deviceSerial. Task failed")
        }

        override fun install(base: Apk, splits: List<Apk>) {
            TODO("Install with root")
        }

        override fun uninstall(packageName: String) {
            TODO("Uninstall with root")
        }
    }

    class UserAdb(deviceSerial: String, override val logger: CliLogger? = null) : Adb(deviceSerial) {
        private val replaceRegex = Regex("\\D+") // all non-digits
        override fun install(base: Apk, splits: List<Apk>) {
            /**
             * Class storing the information required for the installation of an apk.
             *
             * @param apk The apk.
             * @param size The size of the apk file. Inferred by default from [apk].
             */
            data class ApkInfo(val apk: Apk, val size: Long = Files.size(apk.file.toPath()))

            val sizes = buildList {
                val add = { apk: Apk -> add(ApkInfo(apk, Files.size(apk.file.toPath()))) }

                add(base)
                for (split in splits) add(split)
            }

            val installTargetPath = "/data/local/tmp/"

            device.run("pm install-create -S ${sizes.sumOf { it.size }}")
                .replace(replaceRegex, "")
                .also { sid ->
                    logger?.info("Created session $sid")

                    sizes.onEachIndexed { index, (apk, size) ->
                        val installTargetFilePath = "$installTargetPath${apk.file.name}"

                        with(device) {
                            copyFile(apk.file, installTargetFilePath)

                            logger?.info("Staging $installTargetFilePath")
                            run("pm install-write -S $size $sid $index $installTargetFilePath")
                        }
                    }.also {
                        logger?.info("Committing session $sid: ${device.run("pm install-commit $sid")}")
                    }.forEach { (apk, _) ->
                        device.run("rm $installTargetPath${apk.file.name}")
                    }
                }
        }

        override fun uninstall(packageName: String) {
            logger?.info("Uninstalling $packageName")

            packageManager.uninstall(Package(packageName))
        }
    }

    /**
     * Apk file for [Adb].
     *
     * @param filePath The path to the [Apk] file.
     */
    internal class Apk(filePath: String) {
        val file = File(filePath)
    }
}