package app.revanced.cli.command.utility

import app.revanced.library.installation.installer.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import picocli.CommandLine.*
import java.io.File
import java.util.logging.Logger

@Command(
    name = "install",
    description = ["Install an APK file to devices with the supplied ADB device serials"],
)
internal object InstallCommand : Runnable {
    private val logger = Logger.getLogger(this::class.java.name)

    @Parameters(
        description = ["ADB device serials. If not supplied, the first connected device will be used."],
        arity = "0..*",
    )
    private var deviceSerials: Array<String>? = null

    @Option(
        names = ["-a", "--apk"],
        description = ["APK file to be installed"],
        required = true,
    )
    private lateinit var apk: File

    @Option(
        names = ["-m", "--mount"],
        description = ["Mount the supplied APK file over the app with the supplied package name"],
    )
    private var packageName: String? = null

    override fun run() {
        suspend fun install(deviceSerial: String? = null) {
            val result = try {
                if (packageName != null) {
                    AdbRootInstaller(deviceSerial)
                } else {
                    AdbInstaller(deviceSerial)
                }.install(Installer.Apk(apk, packageName))
            } catch (e: Exception) {
                logger.severe(e.toString())
            }

            when (result) {
                RootInstallerResult.FAILURE ->
                    logger.severe("Failed to mount the APK file")
                is AdbInstallerResult.Failure ->
                    logger.severe(result.exception.toString())
                else ->
                    logger.info("Installed the APK file")
            }
        }

        runBlocking {
            deviceSerials?.map { async { install(it) } }?.awaitAll() ?: install()
        }
    }
}
