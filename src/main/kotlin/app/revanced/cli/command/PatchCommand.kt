package app.revanced.cli.command

import app.revanced.library.ApkUtils
import app.revanced.library.ApkUtils.applyTo
import app.revanced.library.installation.installer.*
import app.revanced.library.setOptions
import app.revanced.patcher.patch.Patch
import app.revanced.patcher.patch.loadPatches
import app.revanced.patcher.patcher
import kotlinx.coroutines.runBlocking
import picocli.CommandLine
import picocli.CommandLine.ArgGroup
import picocli.CommandLine.Help.Visibility.ALWAYS
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.Spec
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.logging.Logger

@CommandLine.Command(
    name = "patch",
    description = ["Patch an APK file."],
)
internal object PatchCommand : Runnable {
    private val logger = Logger.getLogger(this::class.java.name)

    @Spec
    private lateinit var spec: CommandSpec

    @ArgGroup(exclusive = false, multiplicity = "0..*")
    private var selection = mutableSetOf<Selection>()

    internal class Selection {
        @ArgGroup(exclusive = false)
        internal var enabled: EnableSelection? = null

        internal class EnableSelection {
            @ArgGroup(multiplicity = "1")
            internal lateinit var selector: EnableSelector

            internal class EnableSelector {
                @CommandLine.Option(
                    names = ["-e", "--enable"],
                    description = ["Name of the patch."],
                    required = true,
                )
                internal var name: String? = null

                @CommandLine.Option(
                    names = ["--ei"],
                    description = ["Index of the patch in the combined list of the supplied RVP files."],
                    required = true,
                )
                internal var index: Int? = null
            }

            @CommandLine.Option(
                names = ["-O", "--options"],
                description = ["Option values keyed by option keys."],
                mapFallbackValue = CommandLine.Option.NULL_VALUE,
                converter = [OptionKeyConverter::class, OptionValueConverter::class],
            )
            internal var options = mutableMapOf<String, Any?>()
        }

        @ArgGroup(exclusive = false)
        internal var disable: DisableSelection? = null

        internal class DisableSelection {
            @ArgGroup(multiplicity = "1")
            internal lateinit var selector: DisableSelector

            internal class DisableSelector {
                @CommandLine.Option(
                    names = ["-d", "--disable"],
                    description = ["Name of the patch."],
                    required = true,
                )
                internal var name: String? = null

                @CommandLine.Option(
                    names = ["--di"],
                    description = ["Index of the patch in the combined list of the supplied RVP files."],
                    required = true,
                )
                internal var index: Int? = null
            }
        }
    }

    @CommandLine.Option(
        names = ["--exclusive"],
        description = ["Disable all patches except the ones enabled."],
        showDefaultValue = ALWAYS,
    )
    private var exclusive = false

    @CommandLine.Option(
        names = ["-f", "--force"],
        description = ["Don't check for compatibility with the supplied APK's version."],
        showDefaultValue = ALWAYS,
    )
    private var force: Boolean = false

    private var outputFilePath: File? = null

    @CommandLine.Option(
        names = ["-o", "--out"],
        description = ["Path to save the patched APK file to. Defaults to the same path as the supplied APK file."],
    )
    @Suppress("unused")
    private fun setOutputFilePath(outputFilePath: File?) {
        this.outputFilePath = outputFilePath?.absoluteFile
    }

    @ArgGroup(exclusive = false, multiplicity = "0..1")
    internal var installation: Installation? = null

    internal class Installation {
        @CommandLine.Option(
            names = ["-i", "--install"],
            required = true,
            description = ["Serial of the ADB device to install to. If not specified, the first connected device will be used."],
            fallbackValue = "", // Empty string is used to select the first of connected devices.
            arity = "0..1",
        )
        internal var deviceSerial: String? = null

        @CommandLine.Option(
            names = ["--mount"],
            required = false,
            description = ["Install the patched APK file by mounting."],
            showDefaultValue = ALWAYS,
        )
        internal var mount: Boolean = false
    }

    @CommandLine.Option(
        names = ["--keystore"],
        description = [
            "Path to the keystore file containing a private key and certificate pair to sign the patched APK file with. " +
                "Defaults to the same directory as the supplied APK file.",
        ],
    )
    private var keyStoreFilePath: File? = null

    @CommandLine.Option(
        names = ["--keystore-password"],
        description = ["Password of the keystore. Empty password by default."],
    )
    private var keyStorePassword: String? = null // Empty password by default

    @CommandLine.Option(
        names = ["--keystore-entry-alias"],
        description = ["Alias of the private key and certificate pair keystore entry."],
        showDefaultValue = ALWAYS,
    )
    private var keyStoreEntryAlias = "ReVanced Key"

    @CommandLine.Option(
        names = ["--keystore-entry-password"],
        description = ["Password of the keystore entry."],
    )
    private var keyStoreEntryPassword = "" // Empty password by default

    @CommandLine.Option(
        names = ["--signer"],
        description = ["The name of the signer to sign the patched APK file with."],
        showDefaultValue = ALWAYS,
    )
    private var signer = "ReVanced"

    @CommandLine.Option(
        names = ["-t", "--temporary-files-path"],
        description = ["Path to store temporary files."],
    )
    private var temporaryFilesPath: File? = null

    private var aaptBinaryPath: File? = null

    @CommandLine.Option(
        names = ["--purge"],
        description = ["Purge temporary files directory after patching."],
        showDefaultValue = ALWAYS,
    )
    private var purge: Boolean = false

    @CommandLine.Parameters(
        description = ["APK file to patch."],
        arity = "1",
    )
    @Suppress("unused")
    private fun setApk(apk: File) {
        if (!apk.exists()) {
            throw CommandLine.ParameterException(
                spec.commandLine(),
                "APK file ${apk.path} does not exist",
            )
        }
        this.apk = apk
    }

    private lateinit var apk: File

    @CommandLine.Option(
        names = ["-p", "--patches"],
        description = ["One or more path to RVP files."],
        required = true,
    )
    @Suppress("unused")
    private fun setPatchesFile(patchesFiles: Set<File>) {
        patchesFiles.firstOrNull { !it.exists() }?.let {
            throw CommandLine.ParameterException(spec.commandLine(), "${it.name} can't be found")
        }
        this.patchesFiles = patchesFiles
    }

    private var patchesFiles = emptySet<File>()

    @CommandLine.Option(
        names = ["--custom-aapt2-binary"],
        description = ["Path to a custom AAPT binary to compile resources with."],
    )
    @Suppress("unused")
    private fun setAaptBinaryPath(aaptBinaryPath: File) {
        if (!aaptBinaryPath.exists()) {
            throw CommandLine.ParameterException(
                spec.commandLine(),
                "AAPT binary ${aaptBinaryPath.name} does not exist",
            )
        }
        this.aaptBinaryPath = aaptBinaryPath
    }

    override fun run() {
        // region Setup

        val outputFilePath =
            outputFilePath ?: File("").absoluteFile.resolve(
                "${apk.nameWithoutExtension}-patched.${apk.extension}",
            )

        val temporaryFilesPath =
            temporaryFilesPath ?: outputFilePath.parentFile.resolve(
                "${outputFilePath.nameWithoutExtension}-temporary-files",
            )

        val keystoreFilePath =
            keyStoreFilePath ?: outputFilePath.parentFile
                .resolve("${outputFilePath.nameWithoutExtension}.keystore")

        val installer = if (installation?.deviceSerial != null) {
            val deviceSerial = installation?.deviceSerial!!.ifEmpty { null }

            try {
                if (installation?.mount == true) {
                    AdbRootInstaller(deviceSerial)
                } else {
                    AdbInstaller(deviceSerial)
                }
            } catch (_: DeviceNotFoundException) {
                if (deviceSerial?.isNotEmpty() == true) {
                    logger.severe(
                        "Device with serial $deviceSerial not found to install to. " +
                            "Ensure the device is connected and the serial is correct when using the --install option.",
                    )
                } else {
                    logger.severe(
                        "No device has been found to install to. " +
                            "Ensure a device is connected when using the --install option.",
                    )
                }

                return
            }
        } else {
            null
        }

        // endregion

        // region Load patches

        logger.info("Loading patches")

        val patches = loadPatches(patchesFiles = patchesFiles.toTypedArray()) { file, throwable ->
            logger.severe("Failed to load patches from ${file.path}:\n${throwable.stackTraceToString()}")
        }

        // endregion

        val patcherTemporaryFilesPath = temporaryFilesPath.resolve("patcher")

        lateinit var _packageName: String

        val patch = patcher(
            apk,
            patcherTemporaryFilesPath,
            aaptBinaryPath,
            patcherTemporaryFilesPath.absolutePath,
        ) { packageName, versionName ->
            _packageName = packageName

            val filteredPatches = patches.filterPatchSelection(packageName, versionName)

            logger.info("Setting patch options")

            val patchesList = patches.toList()

            selection.filter { it.enabled != null }.associate {
                val enabledSelection = it.enabled!!
                val name = enabledSelection.selector.name ?: patchesList[enabledSelection.selector.index!!].name!!

                name to enabledSelection.options
            }.let(filteredPatches::setOptions)

            filteredPatches
        }

        val patchesResult = patch { patchResult ->
            val exception = patchResult.exception
                ?: return@patch logger.info("\"${patchResult.patch}\" succeeded")

            StringWriter().use { writer ->
                exception.printStackTrace(PrintWriter(writer))

                logger.severe("\"${patchResult.patch}\" failed:\n$writer")
            }
        }

        // region Save.

        apk.copyTo(temporaryFilesPath.resolve(apk.name), overwrite = true).let {
            patchesResult.applyTo(it)

            if (installation?.mount != true) {
                ApkUtils.signApk(
                    it,
                    outputFilePath,
                    signer,
                    ApkUtils.KeyStoreDetails(
                        keystoreFilePath,
                        keyStorePassword,
                        keyStoreEntryAlias,
                        keyStoreEntryPassword,
                    ),
                )
            } else {
                it.copyTo(outputFilePath, overwrite = true)
            }
        }

        logger.info("Saved to $outputFilePath")

        // endregion

        // region Install.

        installation?.deviceSerial?.let {
            runBlocking {
                when (val result = installer!!.install(Installer.Apk(outputFilePath, _packageName))) {
                    RootInstallerResult.FAILURE -> logger.severe("Failed to mount the patched APK file")
                    is AdbInstallerResult.Failure -> logger.severe(result.exception.toString())
                    else -> logger.info("Installed the patched APK file")
                }
            }
        }

        // endregion

        if (purge) {
            logger.info("Purging temporary files")
            purge(temporaryFilesPath)
        }
    }

    /**
     * Filter the patches based on the selection.
     *
     * @param packageName The package name of the APK file to be patched.
     * @param packageVersion The version of the APK file to be patched.
     * @return The filtered patches.
     */
    private fun Set<Patch>.filterPatchSelection(
        packageName: String,
        packageVersion: String,
    ) = buildSet {
        val enabledPatchesByName =
            selection.mapNotNull { it.enabled?.selector?.name }.toSet()
        val enabledPatchesByIndex =
            selection.mapNotNull { it.enabled?.selector?.index }.toSet()

        val disabledPatches =
            selection.mapNotNull { it.disable?.selector?.name }.toSet()
        val disabledPatchesByIndex =
            selection.mapNotNull { it.disable?.selector?.index }.toSet()

        this@filterPatchSelection.withIndex().forEach patchLoop@{ (i, patch) ->
            val patchName = patch.name!!

            val isManuallyDisabled = patchName in disabledPatches || i in disabledPatchesByIndex
            if (isManuallyDisabled) return@patchLoop logger.info("\"$patchName\" disabled manually")

            // Make sure the patch is compatible with the supplied APK files package name and version.
            patch.compatiblePackages?.let { packages ->
                packages.singleOrNull { (name, _) -> name == packageName }?.let { (_, versions) ->
                    if (versions?.isEmpty() == true) {
                        return@patchLoop logger.warning("\"$patchName\" incompatible with \"$packageName\"")
                    }

                    val matchesVersion =
                        force || versions?.let { it.any { version -> version == packageVersion } } ?: true

                    if (!matchesVersion) {
                        return@patchLoop logger.warning(
                            "\"$patchName\" incompatible with $packageName $packageVersion " +
                                "but compatible with " +
                                packages.joinToString("; ") { (packageName, versions) ->
                                    packageName + " " + versions!!.joinToString(", ")
                                },
                        )
                    }
                } ?: return@patchLoop logger.fine(
                    "\"$patchName\" incompatible with $packageName. " +
                        "It is only compatible with " +
                        packages.joinToString(", ") { (name, _) -> name },
                )

                return@let
            } ?: logger.fine("\"$patchName\" has no package constraints")

            val isEnabled = !exclusive && patch.use
            val isManuallyEnabled = patchName in enabledPatchesByName || i in enabledPatchesByIndex

            if (!(isEnabled || isManuallyEnabled)) {
                return@patchLoop logger.info("\"$patchName\" disabled")
            }

            add(patch)

            logger.fine("\"$patchName\" added")
        }
    }

    private fun purge(resourceCachePath: File) {
        val result =
            if (resourceCachePath.deleteRecursively()) {
                "Purged resource cache directory"
            } else {
                "Failed to purge resource cache directory"
            }
        logger.info(result)
    }
}
