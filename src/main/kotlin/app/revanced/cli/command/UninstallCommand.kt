package app.revanced.cli.command

import app.revanced.utils.adb.AdbManager
import picocli.CommandLine.*
import picocli.CommandLine.Help.Visibility.ALWAYS


@Command(
    name = "uninstall",
    description = ["Uninstall a patched APK file from the devices with the supplied ADB device serials"]
)
internal object UninstallCommand : Runnable {
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
                AdbManager.RootAdbManager(deviceSerial, logger)
            } else {
                AdbManager.UserAdbManager(deviceSerial, logger)
            }.uninstall(packageName)
        }
    } catch (e: AdbManager.DeviceNotFoundException) {
        logger.error(e.toString())
    }
}