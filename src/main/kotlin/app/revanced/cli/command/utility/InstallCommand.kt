package app.revanced.cli.command.utility

import app.revanced.library.installation.installer.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import picocli.CommandLine
import picocli.CommandLine.*
import java.io.File
import picocli.CommandLine.ParameterException
import java.util.logging.Logger

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

    @Option(
        names = ["--splits"],
        description = ["Paths to split APK files, keyed by split name (e.g. --splits split_config.arm64_v8a=split_arm64.apk)."],
    )
    @Suppress("unused")
    private fun setSplitApkFiles(splitApkFiles: Map<String, File>) {
        splitApkFiles.forEach { (splitName, splitFile) ->
            if (!splitFile.exists()) {
                throw ParameterException(
                    CommandLine(this),
                    "Split APK file for $splitName does not exist: ${splitFile.path}",
                )
            }
        }

        this.splitApkFiles = splitApkFiles.toMutableMap()
    }

    private var splitApkFiles = mutableMapOf<String, File>()

    override fun run() {
        suspend fun install(deviceSerial: String? = null) {
            val result = try {
                if (packageName != null) {
                    AdbRootInstaller(deviceSerial)
                } else {
                    AdbInstaller(deviceSerial)
                }.install(Installer.Apk(apk, packageName, splitApkFiles))
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
