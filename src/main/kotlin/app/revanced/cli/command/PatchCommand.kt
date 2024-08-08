package app.revanced.cli.command

import app.revanced.library.ApkUtils
import app.revanced.library.ApkUtils.applyTo
import app.revanced.library.Options
import app.revanced.library.Options.setOptions
import app.revanced.library.installation.installer.*
import app.revanced.patcher.Patcher
import app.revanced.patcher.PatcherConfig
import app.revanced.patcher.patch.Patch
import app.revanced.patcher.patch.loadPatchesFromJar
import kotlinx.coroutines.runBlocking
import picocli.CommandLine
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
    private val logger = Logger.getLogger(PatchCommand::class.java.name)

    @Spec
    lateinit var spec: CommandSpec // injected by picocli

    private lateinit var apk: File

    private var patchBundles = emptySet<File>()

    @CommandLine.Option(
        names = ["-i", "--include"],
        description = ["List of patches to include."],
    )
    private var includedPatches = hashSetOf<String>()

    @CommandLine.Option(
        names = ["--ii"],
        description = ["List of patches to include by their index in relation to the supplied patch bundles."],
    )
    private var includedPatchesByIndex = arrayOf<Int>()

    @CommandLine.Option(
        names = ["-e", "--exclude"],
        description = ["List of patches to exclude."],
    )
    private var excludedPatches = hashSetOf<String>()

    @CommandLine.Option(
        names = ["--ei"],
        description = ["List of patches to exclude by their index in relation to the supplied patch bundles."],
    )
    private var excludedPatchesByIndex = arrayOf<Int>()

    @CommandLine.Option(
        names = ["--options"],
        description = ["Path to patch options JSON file."],
    )
    private var optionsFile: File? = null

    @CommandLine.Option(
        names = ["--exclusive"],
        description = ["Only include patches that are explicitly specified to be included."],
        showDefaultValue = ALWAYS,
    )
    private var exclusive = false

    @CommandLine.Option(
        names = ["-f", "--force"],
        description = ["Bypass compatibility checks for the supplied APK's version."],
        showDefaultValue = ALWAYS,
    )
    private var force: Boolean = false

    private var outputFilePath: File? = null

    @CommandLine.Option(
        names = ["-o", "--out"],
        description = ["Path to save the patched APK file to. Defaults to the same directory as the supplied APK file."],
    )
    @Suppress("unused")
    private fun setOutputFilePath(outputFilePath: File?) {
        this.outputFilePath = outputFilePath?.absoluteFile
    }

    @CommandLine.Option(
        names = ["-d", "--device-serial"],
        description = ["ADB device serial to install to. If not supplied, the first connected device will be used."],
        // Empty string to indicate that the first connected device should be used.
        fallbackValue = "",
        arity = "0..1",
    )
    private var deviceSerial: String? = null

    @CommandLine.Option(
        names = ["--mount"],
        description = ["Install by mounting the patched APK file."],
        showDefaultValue = ALWAYS,
    )
    private var mount: Boolean = false

    @CommandLine.Option(
        names = ["--keystore"],
        description = [
            "Path to the keystore to sign the patched APK file with. " +
                "Defaults to the same directory as the supplied APK file.",
        ],
    )
    private var keystoreFilePath: File? = null

    @CommandLine.Option(
        names = ["--keystore-password"],
        description = ["The password of the keystore to sign the patched APK file with. Empty password by default."],
    )
    private var keyStorePassword: String? = null // Empty password by default

    @CommandLine.Option(
        names = ["--keystore-entry-alias"],
        description = ["The alias of the keystore entry to sign the patched APK file with."],
        showDefaultValue = ALWAYS,
    )
    private var keyStoreEntryAlias = "ReVanced Key"

    @CommandLine.Option(
        names = ["--keystore-entry-password"],
        description = ["The password of the entry from the keystore for the key to sign the patched APK file with."],
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
        description = ["Path to temporary files directory."],
    )
    private var temporaryFilesPath: File? = null

    private var aaptBinaryPath: File? = null

    @CommandLine.Option(
        names = ["-p", "--purge"],
        description = ["Purge the temporary resource cache directory after patching."],
        showDefaultValue = ALWAYS,
    )
    private var purge: Boolean = false

    @CommandLine.Option(
        names = ["-w", "--warn"],
        description = ["Warn if a patch can not be found in the supplied patch bundles."],
        showDefaultValue = ALWAYS,
    )
    private var warn: Boolean = false

    @CommandLine.Parameters(
        description = ["APK file to be patched."],
        arity = "1..1",
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

    @CommandLine.Option(
        names = ["-m", "--merge"],
        description = ["One or more DEX files or containers to merge into the APK."],
    )
    @Suppress("unused")
    private fun setIntegrations(integrations: Set<File>) {
        logger.warning("The --merge option is not used anymore.")
    }

    @CommandLine.Option(
        names = ["-b", "--patch-bundle"],
        description = ["One or more bundles of patches."],
        required = true,
    )
    @Suppress("unused")
    private fun setPatchBundles(patchBundles: Set<File>) {
        patchBundles.firstOrNull { !it.exists() }?.let {
            throw CommandLine.ParameterException(spec.commandLine(), "Patch bundle ${it.name} does not exist")
        }
        this.patchBundles = patchBundles
    }

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

        val optionsFile =
            optionsFile ?: outputFilePath.parentFile.resolve(
                "${outputFilePath.nameWithoutExtension}-options.json",
            )

        val keystoreFilePath =
            keystoreFilePath ?: outputFilePath.parentFile
                .resolve("${outputFilePath.nameWithoutExtension}.keystore")

        // endregion

        // region Load patches

        logger.info("Loading patches")

        val patches = loadPatchesFromJar(patchBundles)

        // Warn if a patch can not be found in the supplied patch bundles.
        if (warn) {
            patches.map { it.name }.toHashSet().let { availableNames ->
                (includedPatches + excludedPatches).filter { name ->
                    !availableNames.contains(name)
                }
            }.let { unknownPatches ->
                if (unknownPatches.isEmpty()) return@let
                logger.warning("Unknown input of patches:\n${unknownPatches.joinToString("\n")}")
            }
        }

        // endregion

        val patcherTemporaryFilesPath = temporaryFilesPath.resolve("patcher")
        val (packageName, patcherResult) = Patcher(
            PatcherConfig(
                apk,
                patcherTemporaryFilesPath,
                aaptBinaryPath?.path,
                patcherTemporaryFilesPath.absolutePath,
                true,
            ),
        ).use { patcher ->
            val filteredPatches =
                patcher.filterPatchSelection(patches).also { patches ->
                    logger.info("Setting patch options")

                    if (optionsFile.exists()) {
                        patches.setOptions(optionsFile)
                    } else {
                        Options.serialize(patches, prettyPrint = true).let(optionsFile::writeText)
                    }
                }

            patcher += filteredPatches

            // Execute patches.
            runBlocking {
                patcher().collect { patchResult ->
                    val exception = patchResult.exception
                        ?: return@collect logger.info("\"${patchResult.patch}\" succeeded")

                    StringWriter().use { writer ->
                        exception.printStackTrace(PrintWriter(writer))

                        logger.severe("\"${patchResult.patch}\" failed:\n$writer")
                    }
                }
            }

            patcher.context.packageMetadata.packageName to patcher.get()
        }

        // region Save

        apk.copyTo(temporaryFilesPath.resolve(apk.name), overwrite = true).apply {
            patcherResult.applyTo(this)
        }.let { patchedApkFile ->
            if (!mount) {
                ApkUtils.signApk(
                    patchedApkFile,
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
                patchedApkFile.copyTo(outputFilePath, overwrite = true)
            }
        }

        logger.info("Saved to $outputFilePath")

        // endregion

        // region Install

        deviceSerial?.let { it ->
            val deviceSerial = it.ifEmpty { null }

            runBlocking {
                val result = if (mount) {
                    AdbRootInstaller(deviceSerial)
                } else {
                    AdbInstaller(deviceSerial)
                }.install(Installer.Apk(outputFilePath, packageName))

                when (result) {
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
     * Filter the patches to be added to the patcher. The filter is based on the following:
     *
     * @param patches The patches to filter.
     * @return The filtered patches.
     */
    private fun Patcher.filterPatchSelection(patches: Set<Patch<*>>): Set<Patch<*>> =
        buildSet {
            val packageName = context.packageMetadata.packageName
            val packageVersion = context.packageMetadata.packageVersion

            patches.withIndex().forEach patch@{ (i, patch) ->
                val patchName = patch.name!!

                val explicitlyExcluded = excludedPatches.contains(patchName) || excludedPatchesByIndex.contains(i)
                if (explicitlyExcluded) return@patch logger.info("\"$patchName\" excluded manually")

                // Make sure the patch is compatible with the supplied APK files package name and version.
                patch.compatiblePackages?.let { packages ->
                    packages.singleOrNull { (name, _) -> name == packageName }?.let { (_, versions) ->
                        if (versions?.isEmpty() == true) {
                            return@patch logger.warning("\"$patchName\" incompatible with \"$packageName\"")
                        }

                        val matchesVersion = force ||
                            versions?.let { it.any { version -> version == packageVersion } }
                                ?: true

                        if (!matchesVersion) {
                            return@patch logger.warning(
                                "\"$patchName\" incompatible with $packageName $packageVersion " +
                                    "but compatible with " +
                                    packages.joinToString("; ") { (packageName, versions) ->
                                        packageName + " " + versions!!.joinToString(", ")
                                    },
                            )
                        }
                    } ?: return@patch logger.fine(
                        "\"$patchName\" incompatible with $packageName. " +
                            "It is only compatible with " +
                            packages.joinToString(", ") { (name, _) -> name },
                    )

                    return@let
                } ?: logger.fine("\"$patchName\" has no package constraints")

                // If the patch is implicitly used, it will be only included if [exclusive] is false.
                val implicitlyIncluded = !exclusive && patch.use
                // If the patch is explicitly used, it will be included even if [exclusive] is false.
                val explicitlyIncluded = includedPatches.contains(patchName) || includedPatchesByIndex.contains(i)

                val included = implicitlyIncluded || explicitlyIncluded
                if (!included) return@patch logger.info("\"$patchName\" excluded") // Case 1.

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
