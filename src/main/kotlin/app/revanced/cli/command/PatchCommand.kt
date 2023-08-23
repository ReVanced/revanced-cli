package app.revanced.cli.command

import app.revanced.cli.patcher.logging.impl.PatcherLogger
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


@CommandLine.Command(
    name = "patch",
    description = ["Patch the supplied APK file with the supplied patches and integrations"]
)
internal object PatchCommand: Runnable {
    @CommandLine.Parameters(
        description = ["APK file to be patched"],
        arity = "1..1"
    )
    lateinit var apk: File

    @CommandLine.Option(
        names = ["-b", "--patch-bundle"],
        description = ["One or more bundles of patches"],
        required = true
    )
    var patchBundles = emptyList<File>()

    @CommandLine.Option(
        names = ["-m", "--merge"],
        description = ["One or more DEX files or containers to merge into the APK"]
    )
    var integrations = listOf<File>()

    @CommandLine.Option(
        names = ["-i", "--include"],
        description = ["List of patches to include"]
    )
    var includedPatches = arrayOf<String>()

    @CommandLine.Option(
        names = ["-e", "--exclude"],
        description = ["List of patches to exclude"]
    )
    var excludedPatches = arrayOf<String>()

    @CommandLine.Option(
        names = ["--options"],
        description = ["Path to patch options JSON file"],
        showDefaultValue = ALWAYS
    )
    var optionsFile: File = File("options.json")

    @CommandLine.Option(
        names = ["--exclusive"],
        description = ["Only include patches that are explicitly specified to be included"],
        showDefaultValue = ALWAYS
    )
    var exclusive = false

    @CommandLine.Option(
        names = ["--experimental"],
        description = ["Ignore patches incompatibility to versions"],
        showDefaultValue = ALWAYS
    )
    var experimental: Boolean = false

    @CommandLine.Option(
        names = ["-o", "--out"],
        description = ["Path to save the patched APK file to"],
        required = true
    )
    lateinit var outputFilePath: File

    @CommandLine.Option(
        names = ["-d", "--device-serial"],
        description = ["ADB device serial to install to"],
        showDefaultValue = ALWAYS
    )
    var deviceSerial: String? = null

    @CommandLine.Option(
        names = ["--mount"],
        description = ["Install by mounting the patched package"],
        showDefaultValue = ALWAYS
    )
    var mount: Boolean = false

    @CommandLine.Option(
        names = ["--common-name"],
        description = ["The common name of the signer of the patched APK file"],
        showDefaultValue = ALWAYS

    )
    var commonName = "ReVanced"

    @CommandLine.Option(
        names = ["--keystore"],
        description = ["Path to the keystore to sign the patched APK file with"]
    )
    var keystorePath: String? = null

    @CommandLine.Option(
        names = ["--password"],
        description = ["The password of the keystore to sign the patched APK file with"]
    )
    var password = "ReVanced"

    @CommandLine.Option(
        names = ["-r", "--resource-cache"],
        description = ["Path to temporary resource cache directory"],
        showDefaultValue = ALWAYS
    )
    var resourceCachePath = File("revanced-resource-cache")

    @CommandLine.Option(
        names = ["--custom-aapt2-binary"],
        description = ["Path to a custom AAPT binary to compile resources with"]
    )
    var aaptBinaryPath = File("")

    @CommandLine.Option(
        names = ["-p", "--purge"],
        description = ["Purge the temporary resource cache directory after patching"],
        showDefaultValue = ALWAYS
    )
    var purge: Boolean = false

    override fun run() {
        // region Prepare

        if (!apk.exists()) {
            logger.error("Input file ${apk.name} does not exist")
            return
        }

        val adbManager = deviceSerial?.let { serial ->
            if (mount) AdbManager.RootAdbManager(serial, logger) else AdbManager.UserAdbManager(
                serial,
                logger
            )
        }

        // endregion

        // region Load patches

        logger.info("Loading patches")

        val patches = PatchBundleLoader.Jar(*patchBundles.toTypedArray())
        val integrations = integrations

        logger.info("Setting up patch options")

        optionsFile.let {
            if (it.exists()) patches.setOptions(it, logger)
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
                PatcherLogger
            )
        )

        val result = patcher.apply {
            acceptIntegrations(integrations)
            acceptPatches(filterPatchSelection(patches))

            // Execute patches.
            runBlocking {
                apply(false).collect { patchResult ->
                    patchResult.exception?.let {
                        logger.error("${patchResult.patchName} failed:\n${patchResult.exception}")
                    } ?: logger.info("${patchResult.patchName} succeeded")
                }
            }
        }.get()

        patcher.close()

        // endregion

        // region Finish

        val alignAndSignedFile = sign(
            apk.newAlignedFile(
                result,
                resourceCachePath.resolve("${outputFilePath.nameWithoutExtension}_aligned.apk")
            )
        )

        logger.info("Copying to ${outputFilePath.name}")
        alignAndSignedFile.copyTo(outputFilePath, overwrite = true)

        adbManager?.install(AdbManager.Apk(outputFilePath, patcher.context.packageMetadata.packageName))

        if (purge) {
            logger.info("Purging temporary files")
            outputFilePath.delete()
            purge(resourceCachePath)
        }

        // endregion
    }


    /**
     * Filter the patches to be added to the patcher. The filter is based on the following:
     * - [includedPatches] (explicitly included)
     * - [excludedPatches] (explicitly excluded)
     * - [exclusive] (only include patches that are explicitly included)
     * - [experimental] (ignore patches incompatibility to versions)
     * - package name and version of the input APK file (if [experimental] is false)
     *
     * @param patches The patches to filter.
     * @return The filtered patches.
     */
    private fun Patcher.filterPatchSelection(patches: PatchList) = buildList {
        val packageName = context.packageMetadata.packageName
        val packageVersion = context.packageMetadata.packageVersion

        patches.forEach patch@{ patch ->
            val formattedPatchName = patch.patchName.lowercase().replace(" ", "-")

            /**
             * Check if the patch is explicitly excluded.
             *
             * Cases:
             *  1. -e patch.name
             *  2. -i patch.name -e patch.name
             */

            /**
             * Check if the patch is explicitly excluded.
             *
             * Cases:
             *  1. -e patch.name
             *  2. -i patch.name -e patch.name
             */

            val excluded = excludedPatches.contains(formattedPatchName)
            if (excluded) return@patch logger.info("Excluding ${patch.patchName}")

            /**
             * Check if the patch is constrained to packages.
             */

            /**
             * Check if the patch is constrained to packages.
             */

            patch.compatiblePackages?.let { packages ->
                packages.singleOrNull { it.name == packageName }?.let { `package` ->
                    /**
                     * Check if the package version matches.
                     * If experimental is true, version matching will be skipped.
                     */

                    /**
                     * Check if the package version matches.
                     * If experimental is true, version matching will be skipped.
                     */

                    val matchesVersion = experimental || `package`.versions.let {
                        it.isEmpty() || it.any { version -> version == packageVersion }
                    }

                    if (!matchesVersion) return@patch logger.warn(
                        "${patch.patchName} is incompatible with version $packageVersion. " +
                                "This patch is only compatible with version " +
                                packages.joinToString(";") { pkg ->
                                    "${pkg.name}: ${pkg.versions.joinToString(", ")}"
                                }
                    )

                } ?: return@patch logger.trace(
                    "${patch.patchName} is incompatible with $packageName. " +
                            "This patch is only compatible with " +
                            packages.joinToString(", ") { `package` -> `package`.name }
                )

                return@let
            } ?: logger.trace("$formattedPatchName: No constraint on packages.")

            /**
             * Check if the patch is explicitly included.
             *
             * Cases:
             *  1. --exclusive
             *  2. --exclusive -i patch.name
             */

            /**
             * Check if the patch is explicitly included.
             *
             * Cases:
             *  1. --exclusive
             *  2. --exclusive -i patch.name
             */

            val explicitlyIncluded = includedPatches.contains(formattedPatchName)

            val implicitlyIncluded = !exclusive && patch.include // Case 3.
            val exclusivelyIncluded = exclusive && explicitlyIncluded // Case 2.

            val included = implicitlyIncluded || exclusivelyIncluded
            if (!included) return@patch logger.info("${patch.patchName} excluded by default") // Case 1.

            logger.trace("Adding $formattedPatchName")

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
        result: PatcherResult,
        outputFile: File
    ): File {
        logger.info("Aligning $name")

        if (outputFile.exists()) outputFile.delete()

        ZipFile(outputFile).use { file ->
            result.dexFiles.forEach {
                file.addEntryCompressData(
                    ZipEntry.createWithName(it.name),
                    it.stream.readBytes()
                )
            }

            result.resourceFile?.let {
                file.copyEntriesFromFileAligned(
                    ZipFile(it),
                    ZipAligner::getEntryAlignment
                )
            }

            // TODO: Do not compress result.doNotCompress

            file.copyEntriesFromFileAligned(
                ZipFile(this),
                ZipAligner::getEntryAlignment
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
    private fun sign(inputFile: File) = if (mount)
        inputFile
    else {
        logger.info("Signing ${inputFile.name}")

        val keyStoreFilePath = keystorePath ?: outputFilePath
            .absoluteFile.parentFile.resolve("${outputFilePath.nameWithoutExtension}.keystore").canonicalPath

        val options = SigningOptions(
            commonName,
            password,
            keyStoreFilePath
        )

        ApkSigner(options)
            .signApk(
                inputFile,
                resourceCachePath.resolve("${outputFilePath.nameWithoutExtension}_signed.apk")
            )
    }

    private fun purge(resourceCachePath: File) {
        val result = if (resourceCachePath.deleteRecursively())
            "Purged resource cache directory"
        else
            "Failed to purge resource cache directory"
        logger.info(result)
    }
}