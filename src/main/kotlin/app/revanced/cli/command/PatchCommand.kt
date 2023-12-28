package app.revanced.cli.command

import app.revanced.library.ApkUtils
import app.revanced.library.Options
import app.revanced.library.Options.setOptions
import app.revanced.library.adb.AdbManager
import app.revanced.patcher.PatchBundleLoader
import app.revanced.patcher.PatchSet
import app.revanced.patcher.Patcher
import app.revanced.patcher.PatcherOptions
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

    private var integrations = listOf<File>()

    private var patchBundles = emptyList<File>()

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
    private fun setOutputFilePath(outputFilePath: File?) {
        this.outputFilePath = outputFilePath?.absoluteFile
    }

    @CommandLine.Option(
        names = ["-d", "--device-serial"],
        description = ["ADB device serial to install to. If not supplied, the first connected device will be used."],
        fallbackValue = "", // Empty string to indicate that the first connected device should be used.
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

    // key store password
    @CommandLine.Option(
        names = ["--keystore-password"],
        description = ["The password of the keystore to sign the patched APK file with. Empty password by default."],
    )
    private var keyStorePassword: String? = null // Empty password by default

    @CommandLine.Option(
        names = ["--alias"],
        description = ["The alias of the key from the keystore to sign the patched APK file with."],
        showDefaultValue = ALWAYS,
    )
    private var alias = "ReVanced Key"

    @CommandLine.Option(
        names = ["--keystore-entry-password"],
        description = ["The password of the entry from the keystore for the key to sign the patched APK file with."],
    )
    private var password = "" // Empty password by default

    @CommandLine.Option(
        names = ["--signer"],
        description = ["The name of the signer to sign the patched APK file with."],
        showDefaultValue = ALWAYS,
    )
    private var signer = "ReVanced"

    @CommandLine.Option(
        names = ["-r", "--resource-cache"],
        description = ["Path to temporary resource cache directory."],
    )
    private var resourceCachePath: File? = null

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
                "APK file ${apk.name} does not exist",
            )
        }
        this.apk = apk
    }

    @CommandLine.Option(
        names = ["-m", "--merge"],
        description = ["One or more DEX files or containers to merge into the APK."],
    )
    @Suppress("unused")
    private fun setIntegrations(integrations: Array<File>) {
        integrations.firstOrNull { !it.exists() }?.let {
            throw CommandLine.ParameterException(spec.commandLine(), "Integrations file ${it.name} does not exist.")
        }
        this.integrations += integrations
    }

    @CommandLine.Option(
        names = ["-b", "--patch-bundle"],
        description = ["One or more bundles of patches."],
        required = true,
    )
    @Suppress("unused")
    private fun setPatchBundles(patchBundles: Array<File>) {
        patchBundles.firstOrNull { !it.exists() }?.let {
            throw CommandLine.ParameterException(spec.commandLine(), "Patch bundle ${it.name} does not exist")
        }
        this.patchBundles = patchBundles.toList()
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

        val resourceCachePath =
            resourceCachePath ?: outputFilePath.parentFile.resolve(
                "${outputFilePath.nameWithoutExtension}-resource-cache",
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

        val patches = PatchBundleLoader.Jar(*patchBundles.toTypedArray())

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

        Patcher(
            PatcherOptions(
                apk,
                resourceCachePath,
                aaptBinaryPath?.path,
                resourceCachePath.absolutePath,
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

            // region Patch

            val patcherResult =
                patcher.apply {
                    acceptIntegrations(integrations)
                    acceptPatches(filteredPatches.toList())

                    // Execute patches.
                    runBlocking {
                        apply(false).collect { patchResult ->
                            patchResult.exception?.let {
                                StringWriter().use { writer ->
                                    it.printStackTrace(PrintWriter(writer))
                                    logger.severe("${patchResult.patch.name} failed:\n$writer")
                                }
                            } ?: logger.info("${patchResult.patch.name} succeeded")
                        }
                    }
                }.get()

            // endregion

            // region Save

            val alignedFile =
                resourceCachePath.resolve(apk.name).apply {
                    ApkUtils.copyAligned(apk, this, patcherResult)
                }

            if (!mount) {
                ApkUtils.sign(
                    alignedFile,
                    outputFilePath,
                    ApkUtils.SigningOptions(
                        keystoreFilePath,
                        keyStorePassword,
                        alias,
                        password,
                        signer,
                    ),
                )
            } else {
                alignedFile.renameTo(outputFilePath)
            }

            logger.info("Saved to $outputFilePath")

            // endregion

            // region Install

            deviceSerial?.let { serial ->
                AdbManager.getAdbManager(deviceSerial = serial.ifEmpty { null }, mount)
            }?.install(AdbManager.Apk(outputFilePath, patcher.context.packageMetadata.packageName))

            // endregion
        }

        if (purge) {
            logger.info("Purging temporary files")
            purge(resourceCachePath)
        }
    }

    /**
     * Filter the patches to be added to the patcher. The filter is based on the following:
     *
     * @param patches The patches to filter.
     * @return The filtered patches.
     */
    private fun Patcher.filterPatchSelection(patches: PatchSet): PatchSet =
        buildSet {
            val packageName = context.packageMetadata.packageName
            val packageVersion = context.packageMetadata.packageVersion

            patches.withIndex().forEach patch@{ (i, patch) ->
                val patchName = patch.name!!

                val explicitlyExcluded = excludedPatches.contains(patchName) || excludedPatchesByIndex.contains(i)
                if (explicitlyExcluded) return@patch logger.info("Excluding $patchName")

                // Make sure the patch is compatible with the supplied APK files package name and version.
                patch.compatiblePackages?.let { packages ->
                    packages.singleOrNull { it.name == packageName }?.let { `package` ->
                        val matchesVersion =
                            force || `package`.versions?.let {
                                it.any { version -> version == packageVersion }
                            } ?: true

                        if (!matchesVersion) {
                            return@patch logger.warning(
                                "$patchName is incompatible with version $packageVersion. " +
                                    "This patch is only compatible with version " +
                                    packages.joinToString(";") { pkg ->
                                        pkg.versions!!.joinToString(", ")
                                    },
                            )
                        }
                    } ?: return@patch logger.fine(
                        "$patchName is incompatible with $packageName. " +
                            "This patch is only compatible with " +
                            packages.joinToString(", ") { `package` -> `package`.name },
                    )

                    return@let
                } ?: logger.fine("$patchName has no constraint on packages.")

                // If the patch is implicitly used, it will be only included if [exclusive] is false.
                val implicitlyIncluded = !exclusive && patch.use
                // If the patch is explicitly used, it will be included even if [exclusive] is false.
                val explicitlyIncluded = includedPatches.contains(patchName) || includedPatchesByIndex.contains(i)

                val included = implicitlyIncluded || explicitlyIncluded
                if (!included) return@patch logger.info("$patchName excluded") // Case 1.

                logger.fine("Adding $patchName")

                add(patch)
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
