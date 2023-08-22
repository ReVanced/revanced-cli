package app.revanced.cli.command

import app.revanced.utils.adb.AdbManager
import picocli.CommandLine
import picocli.CommandLine.Help.Visibility.ALWAYS


@CommandLine.Command(
    name = "uninstall",
    description = ["Uninstall a patched package from the devices with the supplied ADB device serials"]
)
class UninstallCommand : Runnable {
    @CommandLine.Parameters(
        description = ["ADB device serials"],
        arity = "1..*"
    )
    lateinit var deviceSerials: Array<String>

    @CommandLine.Option(
        names = ["-p", "--package-name"],
        description = ["Package name to uninstall"],
        required = true
    )
    lateinit var packageName: String

    @CommandLine.Option(
        names = ["-u", "--unmount"],
        description = ["Uninstall by unmounting the patched package"],
        showDefaultValue = ALWAYS
    )
    var unmount: Boolean = false

    override fun run() = try {
        deviceSerials.forEach {deviceSerial ->
            if (unmount) {
                AdbManager.RootAdbManager(deviceSerial, MainCommand.logger)
            } else {
                AdbManager.UserAdbManager(deviceSerial, MainCommand.logger)
            }.uninstall(packageName)
        }
    } catch (e: AdbManager.DeviceNotFoundException) {
        MainCommand.logger.error(e.toString())
    }
}