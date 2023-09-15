package app.revanced.cli.command.utility

import app.revanced.utils.adb.AdbManager
import picocli.CommandLine.*
import java.io.File
import java.util.logging.Logger


@Command(
    name = "install", description = ["Install an APK file to devices with the supplied ADB device serials"]
)
internal object InstallCommand : Runnable {
    private val logger = Logger.getLogger(InstallCommand::class.java.name)

    @Parameters(
        description = ["ADB device serials"], arity = "1..*"
    )
    private lateinit var deviceSerials: Array<String>

    @Option(
        names = ["-a", "--apk"], description = ["APK file to be installed"], required = true
    )
    private lateinit var apk: File

    @Option(
        names = ["-m", "--mount"],
        description = ["Mount the supplied APK file over the app with the supplied package name"],
    )
    private var packageName: String? = null

    override fun run() = try {
        deviceSerials.forEach { deviceSerial ->
            if (packageName != null) {
                AdbManager.RootAdbManager(deviceSerial)
            } else {
                AdbManager.UserAdbManager(deviceSerial)
            }.install(AdbManager.Apk(apk, packageName))
        }
    } catch (e: AdbManager.DeviceNotFoundException) {
        logger.severe(e.toString())
    }
}