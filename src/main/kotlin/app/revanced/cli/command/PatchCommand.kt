package app.revanced.cli.command

import app.revanced.patcher.PatchBundleLoader
import app.revanced.patcher.Patcher
import app.revanced.patcher.PatcherOptions
import app.revanced.patcher.PatcherResult
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.include
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.utils.Options
import app.revanced.utils.Options.setOptions
import app.revanced.utils.adb.AdbManager
import app.revanced.utils.align.ZipAligner
import app.revanced.utils.align.zip.ZipFile
import app.revanced.utils.align.zip.structures.ZipEntry
import app.revanced.utils.signing.ApkSigner
import app.revanced.utils.signing.SigningOptions
import kotlinx.coroutines.runBlocking
import picocli.CommandLine
import picocli.CommandLine.Help.Visibility.ALWAYS
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.logging.Logger


@CommandLine.Command(
    name = "patch", description = ["Patch an APK file"]
)
internal object PatchCommand : Runnable {
    private val logger = Logger.getLogger(PatchCommand::class.java.name)

    @CommandLine.Parameters(
        description = ["APK file to be patched"], arity = "1..1"
    )
    private lateinit var apk: File

    @CommandLine.Option(
        names = ["-b", "--patch-bundle"], description = ["One or more bundles of patches"], required = true
    )
    private var patchBundles = emptyList<File>()

    @CommandLine.Option(
        names = ["-m", "--merge"], description = ["One or more DEX files or containers to merge into the APK"]
    )
    private var integrations = listOf<File>()

    @CommandLine.Option(
        names = ["-i", "--include"], description = ["List of patches to include"]
    )
    private var includedPatches = arrayOf<String>()

    @CommandLine.Option(
        names = ["-e", "--exclude"], description = ["List of patches to exclude"]
    )
    private var excludedPatches = arrayOf<String>()

    @CommandLine.Option(
        names = ["--options"], description = ["Path to patch options JSON file"], showDefaultValue = ALWAYS
    )
    private var optionsFile: File = File("options.json")

    @CommandLine.Option(
        names = ["--exclusive"],
        description = ["Only include patches that are explicitly specified to be included"],
        showDefaultValue = ALWAYS
    )
    private var exclusive = false

    @CommandLine.Option(
        names = ["-f","--force"],
        description = ["Force inclusion of patches that are incompatible with the supplied APK file's version"],
        showDefaultValue = ALWAYS
    )
    private var force: Boolean = false

    @CommandLine.Option(
        names = ["-o", "--out"], description = ["Path to save the patched APK file to"], required = true
    )
    private lateinit var outputFilePath: File

    @CommandLine.Option(
        names = ["-d", "--device-serial"], description = ["ADB device serial to install to"], showDefaultValue = ALWAYS
    )
    private var deviceSerial: String? = null

    @CommandLine.Option(
        names = ["--mount"], description = ["Install by mounting the patched APK file"], showDefaultValue = ALWAYS
    )
    private var mount: Boolean = false

    @CommandLine.Option(
        names = ["--common-name"],
        description = ["The common name of the signer of the patched APK file"],
        showDefaultValue = ALWAYS

    )
    private var commonName = "ReVanced"

    @CommandLine.Option(
        names = ["--keystore"], description = ["Path to the keystore to sign the patched APK file with"]
    )
    private var keystorePath: String? = null

    @CommandLine.Option(
        names = ["--password"], description = ["The password of the keystore to sign the patched APK file with"]
    )
    private var password = "ReVanced"

    @CommandLine.Option(
        names = ["-r", "--resource-cache"],
        description = ["Path to temporary resource cache directory"],
        showDefaultValue = ALWAYS
    )
    private var resourceCachePath = File("revanced-resource-cache")

    @CommandLine.Option(
        names = ["--custom-aapt2-binary"], description = ["Path to a custom AAPT binary to compile resources with"]
    )
    private var aaptBinaryPath = File("")

    @CommandLine.Option(
        names = ["-p", "--purge"],
        description = ["Purge the temporary resource cache directory after patching"],
        showDefaultValue = ALWAYS
    )
    private var purge: Boolean = false

    override fun run() {
        // region Prepare

        if (!apk.exists()) {
            logger.severe("APK file ${apk.name} does not exist")
            return
        }

        integrations.filter { !it.exists() }.let {
            if (it.isEmpty()) return@let

            it.forEach { integration ->
                logger.severe("Integration file ${integration.name} does not exist")
            }
            return
        }

        val adbManager = deviceSerial?.let { serial ->
            if (mount) AdbManager.RootAdbManager(serial)
            else AdbManager.UserAdbManager(serial)
        }

        // endregion

        // region Load patches

        logger.info("Loading patches")

        val patches = PatchBundleLoader.Jar(*patchBundles.toTypedArray())
        val integrations = integrations

        logger.info("Setting patch options")

        optionsFile.let {
            if (it.exists()) patches.setOptions(it)
            else Options.serialize(patches, prettyPrint = true).let(it::writeText)
        }

        // endregion

        // region Patch

        val patcher = Patcher(
            PatcherOptions(
                apk,
                resourceCachePath,
                aaptBinaryPath.path,
                resourceCachePath.absolutePath,
            )
        )

        val result = patcher.apply {
            acceptIntegrations(integrations)
            acceptPatches(filterPatchSelection(patches))

            // Execute patches.
            runBlocking {
                apply(false).collect { patchResult ->
                    patchResult.exception?.let {
                        StringWriter().use { writer ->
                            it.printStackTrace(PrintWriter(writer))
                            logger.severe("${patchResult.patchName} failed: $writer")
                        }
                    } ?: logger.info("${patchResult.patchName} succeeded")
                }
            }
        }.get()

        patcher.close()

        // endregion

        // region Finish

        val alignAndSignedFile = sign(
            apk.newAlignedFile(
                result, resourceCachePath.resolve("${outputFilePath.nameWithoutExtension}_aligned.apk")
            )
        )

        logger.info("Copying to ${outputFilePath.name}")
        alignAndSignedFile.copyTo(outputFilePath, overwrite = true)

        adbManager?.install(AdbManager.Apk(outputFilePath, patcher.context.packageMetadata.packageName))

        if (purge) {
            logger.info("Purging temporary files")
            purge(resourceCachePath)
        }

        // endregion
    }


    /**
     * Filter the patches to be added to the patcher. The filter is based on the following:
     * - [includedPatches] (explicitly included)
     * - [excludedPatches] (explicitly excluded)
     * - [exclusive] (only include patches that are explicitly included)
     * - [force] (ignore patches incompatibility to versions)
     * - Package name and version of the input APK file (if [force] is false)
     *
     * @param patches The patches to filter.
     * @return The filtered patches.
     */
    private fun Patcher.filterPatchSelection(patches: PatchList) = buildList {
        // TODO: Remove this eventually because
        //  patches named "patch-name" and "patch name" will conflict.
        fun String.format() = lowercase().replace(" ", "-")

        val formattedExcludedPatches = excludedPatches.map { it.format() }
        val formattedIncludedPatches = includedPatches.map { it.format() }

        val packageName = context.packageMetadata.packageName
        val packageVersion = context.packageMetadata.packageVersion

        patches.forEach patch@{ patch ->
            val formattedPatchName = patch.patchName.format()

            val explicitlyExcluded = formattedExcludedPatches.contains(formattedPatchName)
            if (explicitlyExcluded) return@patch logger.info("Excluding ${patch.patchName}")

            // Make sure the patch is compatible with the supplied APK files package name and version.
            patch.compatiblePackages?.let { packages ->
                packages.singleOrNull { it.name == packageName }?.let { `package` ->
                    val matchesVersion = force || `package`.versions.let {
                        it.isEmpty() || it.any { version -> version == packageVersion }
                    }

                    if (!matchesVersion) return@patch logger.warning(
                        "${patch.patchName} is incompatible with version $packageVersion. "
                                + "This patch is only compatible with version "
                                + packages.joinToString(";") { pkg ->
                            "${pkg.name}: ${pkg.versions.joinToString(", ")}"
                        }
                    )
                } ?: return@patch logger.fine("${patch.patchName} is incompatible with $packageName. "
                        + "This patch is only compatible with "
                        + packages.joinToString(", ") { `package` -> `package`.name })

                return@let
            } ?: logger.fine("$formattedPatchName: No constraint on packages.")

            // If the patch is implicitly included, it will be only included if [exclusive] is false.
            val implicitlyIncluded = !exclusive && patch.include
            // If the patch is explicitly included, it will be included even if [exclusive] is false.
            val explicitlyIncluded = formattedIncludedPatches.contains(formattedPatchName)

            val included = implicitlyIncluded || explicitlyIncluded
            if (!included) return@patch logger.info("${patch.patchName} excluded by default") // Case 1.

            logger.fine("Adding $formattedPatchName")

            add(patch)
        }
    }

    /**
     * Create a new aligned APK file.
     *
     * @param result The result of the patching process.
     * @param outputFile The file to save the aligned APK to.
     */
    private fun File.newAlignedFile(
        result: PatcherResult, outputFile: File
    ): File {
        logger.info("Aligning $name")

        if (outputFile.exists()) outputFile.delete()

        ZipFile(outputFile).use { file ->
            result.dexFiles.forEach {
                file.addEntryCompressData(
                    ZipEntry.createWithName(it.name), it.stream.readBytes()
                )
            }

            result.resourceFile?.let {
                file.copyEntriesFromFileAligned(
                    ZipFile(it), ZipAligner::getEntryAlignment
                )
            }

            // TODO: Do not compress result.doNotCompress

            file.copyEntriesFromFileAligned(
                ZipFile(this), ZipAligner::getEntryAlignment
            )
        }

        return outputFile
    }

    /**
     * Sign the APK file.
     *
     * @param inputFile The APK file to sign.
     * @return The signed APK file. If [mount] is true, the input file will be returned.
     */
    private fun sign(inputFile: File) = if (mount) inputFile
    else {
        logger.info("Signing ${inputFile.name}")

        val keyStoreFilePath = keystorePath
            ?: outputFilePath.absoluteFile.parentFile.resolve("${outputFilePath.nameWithoutExtension}.keystore").canonicalPath

        val options = SigningOptions(
            commonName, password, keyStoreFilePath
        )

        ApkSigner(options).signApk(
                inputFile, resourceCachePath.resolve("${outputFilePath.nameWithoutExtension}_signed.apk")
            )
    }

    private fun purge(resourceCachePath: File) {
        val result = if (resourceCachePath.deleteRecursively()) "Purged resource cache directory"
        else "Failed to purge resource cache directory"
        logger.info(result)
    }
}