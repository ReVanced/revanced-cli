package app.revanced.cli.command

import app.revanced.cli.aligning.Aligning
import app.revanced.cli.logging.impl.DefaultCliLogger
import app.revanced.cli.patcher.Patcher
import app.revanced.cli.patcher.logging.impl.PatcherLogger
import app.revanced.cli.signing.Signing
import app.revanced.cli.signing.SigningOptions
import app.revanced.patcher.PatcherOptions
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.description
import app.revanced.patcher.extensions.PatchExtensions.options
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.util.patch.impl.JarPatchBundle
import app.revanced.utils.OptionsLoader
import app.revanced.utils.adb.Adb
import picocli.CommandLine.*
import java.io.File
import java.nio.file.Files

private class CLIVersionProvider : IVersionProvider {
    override fun getVersion() = arrayOf(
        MainCommand::class.java.`package`.implementationVersion ?: "unknown"
    )
}

@Command(
    name = "ReVanced-CLI",
    mixinStandardHelpOptions = true,
    versionProvider = CLIVersionProvider::class
)
internal object MainCommand : Runnable {
    val logger = DefaultCliLogger()

    @ArgGroup(exclusive = false, multiplicity = "1")
    lateinit var args: Args

    class Args {
        @Option(names = ["-a", "--apk"], description = ["Input file to be patched"], required = true)
        lateinit var inputFile: File

        @Option(names = ["--uninstall"], description = ["Uninstall the mount variant"])
        var uninstall: Boolean = false

        @Option(names = ["-d", "--deploy-on"], description = ["If specified, deploy to adb device with given name"])
        var deploy: String? = null

        @ArgGroup(exclusive = false)
        var patchArgs: PatchArgs? = null
    }

    class PatchArgs {
        @Option(names = ["-b", "--bundles"], description = ["One or more bundles of patches"], required = true)
        var patchBundles = arrayOf<String>()

        @Option(names = ["--options"], description = ["Configuration file for all patch options"])
        var options: File = File("options.toml")

        @ArgGroup(exclusive = false)
        var listingArgs: ListingArgs? = null

        @ArgGroup(exclusive = false)
        var patchingArgs: PatchingArgs? = null
    }

    class ListingArgs {
        @Option(names = ["-l", "--list"], description = ["List patches only"], required = true)
        var listOnly: Boolean = false

        @Option(names = ["--with-versions"], description = ["List patches with compatible versions"])
        var withVersions: Boolean = false

        @Option(names = ["--with-packages"], description = ["List patches with compatible packages"])
        var withPackages: Boolean = false

        @Option(names = ["--with-descriptions"], description = ["List patches with their descriptions"])
        var withDescriptions: Boolean = true

        @Option(names = ["--with-options"], description = ["List patches with their options"])
        var withOptions: Boolean = true
    }

    class PatchingArgs {
        @Option(names = ["-o", "--out"], description = ["Output file path"], required = true)
        lateinit var outputPath: String

        @Option(names = ["-e", "--exclude"], description = ["Explicitly exclude patches"])
        var excludedPatches = arrayOf<String>()

        @Option(
            names = ["--exclusive"],
            description = ["Only installs the patches you include, not including any patch by default"]
        )
        var defaultExclude = false

        @Option(names = ["-i", "--include"], description = ["Include patches"])
        var includedPatches = arrayOf<String>()

        @Option(names = ["-r", "--resource-patcher"], description = ["Disable patching resources"])
        var disableResourcePatching: Boolean = false

        @Option(names = ["--experimental"], description = ["Disable patch version compatibility patch"])
        var experimental: Boolean = false

        @Option(names = ["-m", "--merge"], description = ["One or more dex file containers to merge"])
        var mergeFiles = listOf<File>()

        @Option(names = ["--mount"], description = ["If specified, instead of installing, mount"])
        var mount: Boolean = false

        @Option(names = ["--cn"], description = ["Overwrite the default CN for the signed file"])
        var cn = "ReVanced"

        @Option(names = ["--keystore"], description = ["File path to your keystore"])
        var keystorePath: String? = null

        @Option(names = ["-p", "--password"], description = ["Overwrite the default password for the signed file"])
        var password = "ReVanced"

        @Option(names = ["-t", "--temp-dir"], description = ["Temporary resource cache directory"])
        var cacheDirectory = "revanced-cache"

        @Option(
            names = ["-c", "--clean"],
            description = ["Clean the temporary resource cache directory. This will be done anyways when running the patcher"]
        )
        var clean: Boolean = false

        @Option(names = ["--custom-aapt2-binary"], description = ["Path to custom aapt2 binary"])
        var aaptPath: String = ""
    }

    override fun run() {
        if (args.patchArgs?.listingArgs?.listOnly == true) return printListOfPatches()
        if (args.uninstall) return uninstall()

        val pArgs = this.args.patchArgs?.patchingArgs ?: return
        val outputFile = File(pArgs.outputPath) // the file to write to

        val allPatches = args.patchArgs!!.patchBundles.flatMap { bundle ->
            JarPatchBundle(bundle).loadPatches()
        }

        OptionsLoader.init(args.patchArgs!!.options, allPatches)

        val patcher = app.revanced.patcher.Patcher(
            PatcherOptions(
                args.inputFile,
                pArgs.cacheDirectory,
                !pArgs.disableResourcePatching,
                pArgs.aaptPath,
                pArgs.cacheDirectory,
                PatcherLogger
            )
        )

        // prepare adb
        val adb: Adb? = args.deploy?.let {
            Adb(outputFile, patcher.data.packageMetadata.packageName, args.deploy!!, !pArgs.mount)
        }

        val patchedFile = File(pArgs.cacheDirectory).resolve("${outputFile.nameWithoutExtension}_raw.apk")

        // start the patcher
        Patcher.start(patcher, patchedFile, allPatches)

        val cacheDirectory = File(pArgs.cacheDirectory)

        // align the file
        val alignedFile = cacheDirectory.resolve("${outputFile.nameWithoutExtension}_aligned.apk")
        Aligning.align(patchedFile, alignedFile)

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

    private fun uninstall() {
        // temporarily get package name using Patcher method
        // fix: abstract options in patcher
        val patcher = app.revanced.patcher.Patcher(
            PatcherOptions(
                args.inputFile,
                "uninstaller-cache",
                false
            )
        )
        File("uninstaller-cache").deleteRecursively()

        val adb: Adb? = args.deploy?.let {
            Adb(File("placeholder_file"), patcher.data.packageMetadata.packageName, args.deploy!!, false)
        }
        adb?.uninstall()
    }

    private fun printListOfPatches() {
        val logged = mutableListOf<String>()
        for (patchBundlePath in args.patchArgs?.patchBundles!!) {
            val patches = JarPatchBundle(patchBundlePath).loadPatches()
            val longestName = patches.maxOfOrNull { it.patchName.length } ?: 25
            for (patch in patches.sortedBy { it.patchName.length }) {
                if (patch.patchName in logged) continue
                for (compatiblePackage in patch.compatiblePackages!!) {
                    val packageEntryStr = buildString {
                        // Add package if flag is set
                        if (args.patchArgs?.listingArgs?.withPackages == true) {
                            val packageName = compatiblePackage.name.substringAfterLast(".").padStart(10)
                            append(packageName)
                            append("\t")
                        }
                        // Add patch name
                        val patchName = patch.patchName.padEnd(longestName)
                        append(patchName)
                        // Add description if flag is set.
                        if (args.patchArgs?.listingArgs?.withDescriptions == true) {
                            append("\t")
                            append(patch.description)
                        }
                        // Add compatible versions, if flag is set
                        if (args.patchArgs?.listingArgs?.withVersions == true) {
                            val compatibleVersions = compatiblePackage.versions.joinToString(separator = ", ")
                            append("\t")
                            append(compatibleVersions)
                        }
                    }

                    logged.add(patch.patchName)
                    logger.info(packageEntryStr)

                    if (args.patchArgs?.listingArgs?.withOptions == true) {
                        val options = patch.options
                        // make sure options is not null and has at least 1 option
                        if (options == null || !options.iterator().hasNext()) continue

                        logger.info("\t options:")
                        for (option in options) {
                            logger.info("\t\t ${option.title} - ${option.description}")
                            logger.info("\t\t\t usage: ${option.key} = \"${option.value ?: "<value>"}\"")
                            if (option.value != null) {
                                logger.info("\t\t\t default: ${option.value}")
                            } else {
                                logger.info("\t\t\t this option has no default value")
                            }
                        }
                    }
                }
            }
        }
    }
}
