package app.revanced.cli.command.utility

import app.revanced.library.installation.installer.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import picocli.CommandLine.*
import java.io.File
import java.util.logging.Logger
import kotlin.system.exitProcess

@Command(
    name = "install",
    description = ["Install an APK file."],
)
internal object InstallCommand : Runnable {
    private val logger = Logger.getLogger(this::class.java.name)

    @Parameters(
        description = ["Serial of ADB devices. If not supplied, the first connected device will be used."],
        arity = "0..*",
    )
    private var deviceSerials: Array<String>? = null

    @Option(
        names = ["-a", "--apk"],
        description = ["APK file to be installed."],
        required = true,
    )
    private lateinit var apk: File

    @Option(
        names = ["-m", "--mount"],
        description = ["Mount the supplied APK file over the app with the supplied package name."],
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
                throw e
            }

            when (result) {
                RootInstallerResult.SUCCESS ->
                    logger.info("Mounted the APK file")
                RootInstallerResult.FAILURE -> {
                    logger.severe("Failed to mount the APK file")
                    throw Exception()
                }
                AdbInstallerResult.Success ->
                    logger.info("Installed the APK file")
                is AdbInstallerResult.Failure -> {
                    logger.severe("Failed to install the APK file: ${result.exception}")
                    throw Exception()
                }
                else -> {
                    logger.severe("Unknown installation result")
                    throw Exception()
                }
            }
        }

        runBlocking {
            try {
                deviceSerials?.map { async { install(it) } }?.awaitAll() ?: install()
            } catch (_: Exception) {
                exitProcess(1)
            }
        }
    }
}
