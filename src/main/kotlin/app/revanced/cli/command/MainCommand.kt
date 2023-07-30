package app.revanced.cli.command

import app.revanced.cli.aligning.Aligning
import app.revanced.cli.logging.impl.DefaultCliLogger
import app.revanced.cli.patcher.Patcher
import app.revanced.cli.patcher.logging.impl.PatcherLogger
import app.revanced.cli.signing.Signing
import app.revanced.cli.signing.SigningOptions
import app.revanced.patcher.PatcherOptions
import app.revanced.patcher.data.Context
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.description
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.patch.Patch
import app.revanced.patcher.util.patch.PatchBundle
import app.revanced.utils.Options
import app.revanced.utils.Options.setOptions
import app.revanced.utils.adb.Adb
import picocli.CommandLine.*
import java.io.File
import java.nio.file.Files

/**
 * Alias for return type of [PatchBundle.loadPatches].
 */
internal typealias PatchList = List<Class<out Patch<Context>>>

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

    class Args {
        // TODO: Move this so it is not required when listing patches
        @Option(names = ["-a", "--apk"], description = ["APK file to be patched"], required = true)
        lateinit var inputFile: File

        @Option(names = ["--unmount"], description = ["Unmount a patched APK file"])
        var unmount: Boolean = false

        @Option(
            names = ["-d", "--deploy"],
            description = ["Deploy to the specified device that is connected via ADB"]
        )
        var deploy: String? = null

        @ArgGroup(exclusive = false)
        var patchArgs: PatchArgs? = null
    }

    class PatchArgs {
        @Option(names = ["-b", "--bundle"], description = ["One or more bundles of patches"], required = true)
        var patchBundles = arrayOf<String>()

        @Option(names = ["--options"], description = ["Path to patch options JSON file"])
        var optionsFile: File = File("options.json")

        @ArgGroup(exclusive = false)
        var listingArgs: ListingArgs? = null

        @ArgGroup(exclusive = false)
        var patchingArgs: PatchingArgs? = null
    }

    class ListingArgs {
        @Option(names = ["-l", "--list"], description = ["List patches"], required = true)
        var listOnly: Boolean = false

        @Option(names = ["--with-versions"], description = ["List patches with version compatibilities"])
        var withVersions: Boolean = false

        @Option(names = ["--with-packages"], description = ["List patches with package compatibilities"])
        var withPackages: Boolean = false
    }

    class PatchingArgs {
        @Option(names = ["-o", "--out"], description = ["Path to save the patched APK file to"], required = true)
        lateinit var outputPath: String

        @Option(names = ["-e", "--exclude"], description = ["Exclude patches"])
        var excludedPatches = arrayOf<String>()

        @Option(
            names = ["--exclusive"],
            description = ["Only include patches that were explicitly specified to be included"]
        )
        var exclusive = false

        @Option(names = ["-i", "--include"], description = ["Include patches"])
        var includedPatches = arrayOf<String>()

        @Option(names = ["--experimental"], description = ["Ignore patches incompatibility to versions"])
        var experimental: Boolean = false

        @Option(names = ["-m", "--merge"], description = ["One or more DEX files or containers to merge into the APK"])
        var mergeFiles = listOf<File>()

        @Option(
            names = ["--mount"],
            description = ["Mount the patched APK file over the original file instead of installing it"]
        )
        var mount: Boolean = false

        @Option(names = ["--cn"], description = ["The common name of the signer of the patched APK file"])
        var cn = "ReVanced"

        @Option(names = ["--keystore"], description = ["Path to the keystore to sign the patched APK file with"])
        var keystorePath: String? = null

        @Option(
            names = ["-p", "--password"],
            description = ["The password of the keystore to sign the patched APK file with"]
        )
        var password = "ReVanced"

        @Option(names = ["-t", "--temp-dir"], description = ["Path to temporary resource cache directory"])
        var cacheDirectory = "revanced-cache"

        @Option(
            names = ["-c", "--clean"],
            description = ["Clean up the temporary resource cache directory after patching"]
        )
        var clean: Boolean = false

        @Option(
            names = ["--custom-aapt2-binary"],
            description = ["Path to custom AAPT binary to compile resources with"]
        )
        var aaptPath: String = ""
    }

    override fun run() {
        if (args.patchArgs?.listingArgs?.listOnly == true) return printListOfPatches()
        if (args.unmount) return unmount()

        val pArgs = this.args.patchArgs?.patchingArgs ?: return
        val outputFile = File(pArgs.outputPath) // the file to write to

        val allPatches = args.patchArgs!!.patchBundles.flatMap { bundle ->
            PatchBundle.Jar(bundle).loadPatches()
        }

        args.patchArgs!!.optionsFile.let {
            if (it.exists()) allPatches.setOptions(it, logger)
            else Options.serialize(allPatches, prettyPrint = true).let(it::writeText)
        }

        val patcher = app.revanced.patcher.Patcher(
            PatcherOptions(
                args.inputFile.also { if (!it.exists()) return logger.error("Input file ${args.inputFile} does not exist.") },
                pArgs.cacheDirectory,
                pArgs.aaptPath,
                pArgs.cacheDirectory,
                PatcherLogger
            )
        )

        // prepare adb
        val adb: Adb? = args.deploy?.let {
            Adb(outputFile, patcher.context.packageMetadata.packageName, args.deploy!!, !pArgs.mount)
        }

        // start the patcher
        val result = Patcher.start(patcher, allPatches)

        val cacheDirectory = File(pArgs.cacheDirectory)

        // align the file
        val alignedFile = cacheDirectory.resolve("${outputFile.nameWithoutExtension}_aligned.apk")
        Aligning.align(result, args.inputFile, alignedFile)

        // sign the file
        val finalFile = if (!pArgs.mount) {
            val signedOutput = cacheDirectory.resolve("${outputFile.nameWithoutExtension}_signed.apk")
            Signing.sign(
                alignedFile,
                signedOutput,
                SigningOptions(
                    pArgs.cn,
                    pArgs.password,
                    pArgs.keystorePath ?: outputFile.absoluteFile.parentFile
                        .resolve("${outputFile.nameWithoutExtension}.keystore")
                        .canonicalPath
                )
            )

            signedOutput
        } else
            alignedFile

        // finally copy to the specified output file
        logger.info("Copying ${finalFile.name} to ${outputFile.name}")
        finalFile.copyTo(outputFile, overwrite = true)

        // clean up the cache directory if needed
        if (pArgs.clean)
            cleanUp(pArgs.cacheDirectory)

        // deploy if specified
        adb?.deploy()

        if (pArgs.clean && args.deploy != null) Files.delete(outputFile.toPath())

        logger.info("Finished")
    }

    private fun cleanUp(cacheDirectory: String) {
        val result = if (File(cacheDirectory).deleteRecursively())
            "Cleaned up cache directory"
        else
            "Failed to clean up cache directory"
        logger.info(result)
    }

    private fun unmount() {
        val adb: Adb? = args.deploy?.let {
            Adb(
                File("placeholder_file"),
                app.revanced.patcher.Patcher(PatcherOptions(args.inputFile, "")).context.packageMetadata.packageName,
                args.deploy!!,
                false
            )
        }
        adb?.uninstall()
    }

    private fun printListOfPatches() {
        val logged = mutableListOf<String>()
        for (patchBundlePath in args.patchArgs?.patchBundles!!) for (patch in PatchBundle.Jar(patchBundlePath)
            .loadPatches()) {
            if (patch.patchName in logged) continue
            for (compatiblePackage in patch.compatiblePackages ?: continue) {
                val packageEntryStr = buildString {
                    // Add package if flag is set
                    if (args.patchArgs?.listingArgs?.withPackages == true) {
                        val packageName = compatiblePackage.name.substringAfterLast(".").padStart(10)
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
}
