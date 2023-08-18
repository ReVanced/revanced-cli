package app.revanced.cli.command

import app.revanced.cli.aligning.Aligning
import app.revanced.cli.logging.impl.DefaultCliLogger
import app.revanced.cli.patcher.logging.impl.PatcherLogger
import app.revanced.cli.signing.Signing
import app.revanced.cli.signing.SigningOptions
import app.revanced.patcher.PatchBundleLoader
import app.revanced.patcher.Patcher
import app.revanced.patcher.PatcherOptions
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.description
import app.revanced.patcher.extensions.PatchExtensions.include
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.patch.PatchClass
import app.revanced.utils.Options
import app.revanced.utils.Options.setOptions
import app.revanced.utils.adb.AdbManager
import kotlinx.coroutines.runBlocking
import picocli.CommandLine.*
import java.io.File


internal typealias PatchList = List<PatchClass>

private class CLIVersionProvider : IVersionProvider {
    override fun getVersion() = arrayOf(
        MainCommand::class.java.`package`.implementationVersion ?: "unknown"
    )
}

@Command(
    name = "ReVanced CLI",
    mixinStandardHelpOptions = true,
    versionProvider = CLIVersionProvider::class
)
internal object MainCommand : Runnable {
    val logger = DefaultCliLogger()

    @ArgGroup(exclusive = false, multiplicity = "1")
    lateinit var args: Args

    /**
     * Arguments for the CLI
     */
    class Args {
        @Option(names = ["--uninstall"], description = ["Package name to uninstall"])
        var packageName: String? = null

        @Option(names = ["-d", "--device-serial"], description = ["ADB device serial number to deploy to"])
        var deviceSerial: String? = null

        @Option(names = ["--mount"], description = ["Handle deployments by mounting"])
        var mount: Boolean = false

        @ArgGroup(exclusive = false)
        var patchArgs: PatchArgs? = null

        /**
         * Arguments for patches.
         */
        class PatchArgs {
            @Option(names = ["-b", "--bundle"], description = ["One or more bundles of patches"], required = true)
            var patchBundles = emptyList<File>()

            @ArgGroup(exclusive = false)
            var listingArgs: ListingArgs? = null

            @ArgGroup(exclusive = false)
            var patchingArgs: PatchingArgs? = null

            /**
             * Arguments for patching.
             */
            class PatchingArgs {
                @Option(names = ["-a", "--apk"], description = ["APK file to be patched"], required = true)
                lateinit var inputFile: File

                @Option(
                    names = ["-o", "--out"],
                    description = ["Path to save the patched APK file to"],
                    required = true
                )
                lateinit var outputFilePath: File

                @Option(names = ["--options"], description = ["Path to patch options JSON file"])
                var optionsFile: File = File("options.json")

                @Option(names = ["-e", "--exclude"], description = ["List of patches to exclude"])
                var excludedPatches = arrayOf<String>()

                @Option(
                    names = ["--exclusive"],
                    description = ["Only include patches that are explicitly specified to be included"]
                )
                var exclusive = false

                @Option(names = ["-i", "--include"], description = ["List of patches to include"])
                var includedPatches = arrayOf<String>()

                @Option(names = ["--experimental"], description = ["Ignore patches incompatibility to versions"])
                var experimental: Boolean = false

                @Option(
                    names = ["-m", "--merge"],
                    description = ["One or more DEX files or containers to merge into the APK"]
                )
                var integrations = listOf<File>()

                @Option(names = ["--cn"], description = ["The common name of the signer of the patched APK file"])
                var commonName = "ReVanced"

                @Option(
                    names = ["--keystore"],
                    description = ["Path to the keystore to sign the patched APK file with"]
                )
                var keystorePath: String? = null

                @Option(
                    names = ["-p", "--password"],
                    description = ["The password of the keystore to sign the patched APK file with"]
                )
                var password = "ReVanced"

                @Option(
                    names = ["-r", "--resource-cache"],
                    description = ["Path to temporary resource cache directory"]
                )
                var resourceCachePath = File("revanced-resource-cache")

                @Option(
                    names = ["-c", "--clean"],
                    description = ["Clean up the temporary resource cache directory after patching"]
                )
                var clean: Boolean = false

                @Option(
                    names = ["--custom-aapt2-binary"],
                    description = ["Path to a custom AAPT binary to compile resources with"]
                )
                var aaptBinaryPath = File("")
            }

            /**
             * Arguments for printing patches to the console.
             */
            class ListingArgs {
                @Option(names = ["-l", "--list"], description = ["List patches"], required = true)
                var listOnly: Boolean = false

                @Option(names = ["--with-versions"], description = ["List patches and their compatible versions"])
                var withVersions: Boolean = false

                @Option(names = ["--with-packages"], description = ["List patches and their compatible packages"])
                var withPackages: Boolean = false
            }
        }
    }

    override fun run() {
        val patchArgs = args.patchArgs

        if (patchArgs?.listingArgs?.listOnly == true) return printListOfPatches()
        if (args.packageName != null) return uninstall()

        val patchingArgs = patchArgs?.patchingArgs ?: return

        if (!patchingArgs.inputFile.exists()) return logger.error("Input file ${patchingArgs.inputFile} does not exist.")

        logger.info("Loading patches")

        val patches = PatchBundleLoader.Jar(*patchArgs.patchBundles.toTypedArray())
        val integrations = patchingArgs.integrations

        logger.info("Setting up patch options")

        patchingArgs.optionsFile.let {
            if (it.exists()) patches.setOptions(it, logger)
            else Options.serialize(patches, prettyPrint = true).let(it::writeText)
        }

        val adbManager = args.deviceSerial?.let { serial ->
            if (args.mount) AdbManager.RootAdbManager(serial, logger) else AdbManager.UserAdbManager(serial, logger)
        }

        val patcher = Patcher(
            PatcherOptions(
                patchingArgs.inputFile,
                patchingArgs.resourceCachePath,
                patchingArgs.aaptBinaryPath.absolutePath,
                patchingArgs.resourceCachePath.absolutePath,
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

        val outputFileNameWithoutExtension = patchingArgs.outputFilePath.nameWithoutExtension

        // Align the file.
        val alignedFile = patchingArgs.resourceCachePath.resolve("${outputFileNameWithoutExtension}_aligned.apk")
        Aligning.align(result, patchingArgs.inputFile, alignedFile)

        // Sign the file if needed.
        val finalFile = if (!args.mount) {
            val signedOutput = patchingArgs.resourceCachePath.resolve("${outputFileNameWithoutExtension}_signed.apk")
            Signing.sign(
                alignedFile,
                signedOutput,
                SigningOptions(
                    patchingArgs.commonName,
                    patchingArgs.password,
                    patchingArgs.keystorePath ?: patchingArgs.outputFilePath.absoluteFile.parentFile
                        .resolve("${patchingArgs.outputFilePath.nameWithoutExtension}.keystore")
                        .canonicalPath
                )
            )

            signedOutput
        } else
            alignedFile

        logger.info("Copying ${finalFile.name} to ${patchingArgs.outputFilePath.name}")

        finalFile.copyTo(patchingArgs.outputFilePath, overwrite = true)
        adbManager?.install(AdbManager.Apk(patchingArgs.outputFilePath, patcher.context.packageMetadata.packageName))

        if (patchingArgs.clean) {
            logger.info("Cleaning up temporary files")
            patchingArgs.outputFilePath.delete()
            cleanUp(patchingArgs.resourceCachePath)
        }
    }

    private fun cleanUp(resourceCachePath: File) {
        val result = if (resourceCachePath.deleteRecursively())
            "Cleaned up cache directory"
        else
            "Failed to clean up cache directory"
        logger.info(result)
    }

    /**
     * Uninstall the specified package from the specified device.
     *
     */
    private fun uninstall() = args.deviceSerial?.let { serial ->
        if (args.mount) {
            AdbManager.RootAdbManager(serial, logger)
        } else {
            AdbManager.UserAdbManager(serial, logger)
        }.uninstall(args.packageName!!)
    } ?: logger.error("No device serial specified")

    private fun printListOfPatches() {
        val logged = mutableListOf<String>()
        for (patch in PatchBundleLoader.Jar(*args.patchArgs!!.patchBundles.toTypedArray())) {
            if (patch.patchName in logged) continue
            for (compatiblePackage in patch.compatiblePackages ?: continue) {
                val packageEntryStr = buildString {
                    // Add package if flag is set
                    if (args.patchArgs?.listingArgs?.withPackages == true) {
                        val packageName = compatiblePackage.name.padStart(25)
                        append(packageName)
                        append("\t")
                    }

                    // Add patch name
                    val patchName = patch.patchName.lowercase().replace(" ", "-").padStart(25)
                    append(patchName)

                    // Add description if flag is set.
                    append("\t")
                    append(patch.description)

                    // Add compatible versions, if flag is set
                    if (args.patchArgs?.listingArgs?.withVersions == true) {
                        val compatibleVersions = compatiblePackage.versions.joinToString(separator = ", ")
                        append("\t")
                        append(compatibleVersions)
                    }
                }

                logged.add(patch.patchName)
                logger.info(packageEntryStr)
            }
        }
    }

    private fun Patcher.filterPatchSelection(patches: PatchList) = buildList {
        val packageName = context.packageMetadata.packageName
        val packageVersion = context.packageMetadata.packageVersion
        val patchingArgs = args.patchArgs!!.patchingArgs!!

        patches.forEach patch@{ patch ->
            val formattedPatchName = patch.patchName.lowercase().replace(" ", "-")

            /**
             * Check if the patch is explicitly excluded.
             *
             * Cases:
             *  1. -e patch.name
             *  2. -i patch.name -e patch.name
             */

            val excluded = patchingArgs.excludedPatches.contains(formattedPatchName)
            if (excluded) return@patch logger.info("Excluding ${patch.patchName}")

            /**
             * Check if the patch is constrained to packages.
             */

            patch.compatiblePackages?.let { packages ->
                packages.singleOrNull { it.name == packageName }?.let { `package` ->
                    /**
                     * Check if the package version matches.
                     * If experimental is true, version matching will be skipped.
                     */

                    val matchesVersion = patchingArgs.experimental || `package`.versions.let {
                        it.isEmpty() || it.any { version -> version == packageVersion }
                    }

                    if (!matchesVersion) return@patch logger.warn(
                        "${patch.patchName} is incompatible with version $packageVersion. " +
                                "This patch is only compatible with version " +
                                packages.joinToString(";") { `package` ->
                                    "${`package`.name}: ${`package`.versions.joinToString(", ")}"
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

            val exclusive = patchingArgs.exclusive
            val explicitlyIncluded = patchingArgs.includedPatches.contains(formattedPatchName)

            val implicitlyIncluded = !exclusive && patch.include // Case 3.
            val exclusivelyIncluded = exclusive && explicitlyIncluded // Case 2.

            val included = implicitlyIncluded || exclusivelyIncluded
            if (!included) return@patch logger.info("${patch.patchName} excluded by default") // Case 1.

            logger.trace("Adding $formattedPatchName")

            add(patch)
        }
    }
}