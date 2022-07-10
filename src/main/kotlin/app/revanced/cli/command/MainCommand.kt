package app.revanced.cli.command

import app.revanced.cli.logging.impl.DefaultCliLogger
import app.revanced.cli.patcher.Patcher
import app.revanced.cli.patcher.logging.impl.PatcherLogger
import app.revanced.cli.signing.Signing
import app.revanced.cli.signing.SigningOptions
import app.revanced.patcher.PatcherOptions
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.description
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.util.patch.implementation.JarPatchBundle
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
        var sArgs: StartPatcherArgs? = null
    }

    class StartPatcherArgs {
        @Option(names = ["-b", "--bundles"], description = ["One or more bundles of patches"], required = true)
        var patchBundles = arrayOf<String>()

        @ArgGroup(exclusive = false)
        var lArgs: ListingArgs? = null

        @ArgGroup(exclusive = false)
        var pArgs: PatchingArgs? = null
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
    }

    class PatchingArgs {
        @Option(names = ["-o", "--out"], description = ["Output file path"], required = true)
        lateinit var outputPath: String

        @Option(names = ["-e", "--exclude"], description = ["Explicitly exclude patches"])
        var excludedPatches = arrayOf<String>()

        @Option(names = ["--exclusive"], description = ["Exclusively include patches"])
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

        @Option(names = ["-t", "--temp-dir"], description = ["Temporal resource cache directory"])
        var cacheDirectory = "revanced-cache"

        @Option(
            names = ["-c", "--clean"],
            description = ["Clean the temporal resource cache directory. This will be done anyways when running the patcher"]
        )
        var clean: Boolean = false
    }

    override fun run() {
        if (args.sArgs?.lArgs?.listOnly == true) {
            printListOfPatches()
            return
        }

        if (args.uninstall) {
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

            return
        }

        val _args = args
        val args = args.sArgs?.pArgs ?: return

        val patcher = app.revanced.patcher.Patcher(
            PatcherOptions(
                _args.inputFile,
                args.cacheDirectory,
                !args.disableResourcePatching,
                logger = PatcherLogger
            )
        )

        val outputFile = File(args.outputPath)

        val adb: Adb? = _args.deploy?.let {
            Adb(outputFile, patcher.data.packageMetadata.packageName, _args.deploy!!, !args.mount)
        }
        val patchedFile = if (args.mount) outputFile
        else File(args.cacheDirectory).resolve("${outputFile.nameWithoutExtension}_raw.apk")

        Patcher.start(patcher, patchedFile)

        if (!args.mount) {
            Signing.start(
                patchedFile,
                outputFile,
                SigningOptions(
                    args.cn,
                    args.password,
                    args.keystorePath ?: outputFile.absoluteFile.parentFile
                        .resolve("${outputFile.nameWithoutExtension}.keystore")
                        .canonicalPath
                )
            )
        }

        if (args.clean) File(args.cacheDirectory).deleteRecursively()

        adb?.deploy()

        if (args.clean && _args.deploy != null) Files.delete(outputFile.toPath())

        logger.info("Finished")
    }

    private fun printListOfPatches() {
        for (patchBundlePath in args.sArgs?.patchBundles!!) for (patch in JarPatchBundle(patchBundlePath).loadPatches()) {
            for (compatiblePackage in patch.compatiblePackages!!) {
                val packageEntryStr = buildString {
                    // Add package if flag is set
                    if (args.sArgs?.lArgs?.withPackages == true) {
                        val packageName = compatiblePackage.name.substringAfterLast(".").padStart(10)
                        append(packageName)
                        append("\t")
                    }
                    // Add patch name
                    val patchName = patch.patchName.padStart(25)
                    append(patchName)
                    // Add description if flag is set.
                    if (args.sArgs?.lArgs?.withDescriptions == true) {
                        append("\t")
                        append(patch.description)
                    }
                    // Add compatible versions, if flag is set
                    if (args.sArgs?.lArgs?.withVersions == true) {
                        val compatibleVersions = compatiblePackage.versions.joinToString(separator = ", ")
                        append("\t")
                        append(compatibleVersions)
                    }

                }
                logger.info(packageEntryStr)
            }
        }
    }
}
