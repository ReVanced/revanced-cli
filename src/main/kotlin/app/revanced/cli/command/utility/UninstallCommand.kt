package app.revanced.cli.command.utility

import app.revanced.library.adb.AdbManager
import picocli.CommandLine.*
import picocli.CommandLine.Help.Visibility.ALWAYS
import java.util.logging.Logger

@Command(
    name = "uninstall",
    description = ["Uninstall a patched app from the devices with the supplied ADB device serials"],
)
internal object UninstallCommand : Runnable {
    private val logger = Logger.getLogger(UninstallCommand::class.java.name)

    @Parameters(
        description = ["ADB device serials. If not supplied, the first connected device will be used."],
        arity = "0..*",
    )
    private var deviceSerials: Array<String>? = null

    @Option(
        names = ["-p", "--package-name"],
        description = ["Package name of the app to uninstall"],
        required = true,
    )
    private lateinit var packageName: String

    @Option(
        names = ["-u", "--unmount"],
        description = ["Uninstall by unmounting the patched APK file"],
        showDefaultValue = ALWAYS,
    )
    private var unmount: Boolean = false

    override fun run() {
        fun uninstall(deviceSerial: String? = null) =
            try {
                AdbManager.getAdbManager(deviceSerial, unmount).uninstall(packageName)
            } catch (e: AdbManager.DeviceNotFoundException) {
                logger.severe(e.toString())
            }

        deviceSerials?.forEach { uninstall(it) } ?: uninstall()
    }
}
