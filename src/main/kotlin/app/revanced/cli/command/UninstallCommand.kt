package app.revanced.cli.command

import app.revanced.utils.adb.AdbManager
import picocli.CommandLine.*
import picocli.CommandLine.Help.Visibility.ALWAYS
import java.util.logging.Logger


@Command(
    name = "uninstall",
    description = ["Uninstall a patched APK file from the devices with the supplied ADB device serials"]
)
internal object UninstallCommand : Runnable {
    private val logger = Logger.getLogger(UninstallCommand::class.java.name)

    @Parameters(
        description = ["ADB device serials"],
        arity = "1..*"
    )
    lateinit var deviceSerials: Array<String>

    @Option(
        names = ["-p", "--package-name"],
        description = ["Package name to uninstall"],
        required = true
    )
    lateinit var packageName: String

    @Option(
        names = ["-u", "--unmount"],
        description = ["Uninstall by unmounting the patched package"],
        showDefaultValue = ALWAYS
    )
    var unmount: Boolean = false

    override fun run() = try {
        deviceSerials.forEach {deviceSerial ->
            if (unmount) {
                AdbManager.RootAdbManager(deviceSerial)
            } else {
                AdbManager.UserAdbManager(deviceSerial)
            }.uninstall(packageName)
        }
    } catch (e: AdbManager.DeviceNotFoundException) {
        logger.severe(e.toString())
    }
}