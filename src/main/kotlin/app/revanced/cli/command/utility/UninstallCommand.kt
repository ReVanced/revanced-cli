package app.revanced.cli.command.utility

import app.revanced.library.installation.installer.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
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
        suspend fun uninstall(deviceSerial: String? = null) {
            val result = try {
                if (unmount) {
                    AdbRootInstaller(deviceSerial)
                } else {
                    AdbInstaller(deviceSerial)
                }.uninstall(packageName)
            } catch (e: Exception) {
                logger.severe(e.toString())
            }

            when (result) {
                RootInstallerResult.FAILURE ->
                    logger.severe("Failed to unmount the patched APK file")
                is AdbInstallerResult.Failure ->
                    logger.severe(result.exception.toString())
                else -> logger.info("Uninstalled the patched APK file")
            }
        }

        runBlocking {
            deviceSerials?.map { async { uninstall(it) } }?.awaitAll() ?: uninstall()
        }
    }
}
