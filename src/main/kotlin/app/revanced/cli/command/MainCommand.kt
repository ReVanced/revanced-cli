package app.revanced.cli.command

import app.revanced.cli.patcher.Patcher
import app.revanced.cli.signing.Signing
import app.revanced.patcher.PatcherOptions
import app.revanced.patcher.extensions.PatchExtensions.description
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.util.patch.implementation.JarPatchBundle
import app.revanced.utils.adb.Adb
import picocli.CommandLine.*
import java.io.File
import java.nio.file.Files

@Command(
    name = "ReVanced-CLI", version = ["1.0.0"], mixinStandardHelpOptions = true
)
internal object MainCommand : Runnable {
    @ArgGroup(exclusive = false, multiplicity = "1")
    lateinit var args: Args

    class Args {
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
    }

    class PatchingArgs {
        @Option(names = ["-a", "--apk"], description = ["Input file to be patched"], required = true)
        lateinit var inputFile: File

        @Option(names = ["-o", "--out"], description = ["Output file path"], required = true)
        lateinit var outputPath: String

        @Option(names = ["-e", "--exclude"], description = ["Explicitly exclude patches"])
        var excludedPatches = arrayOf<String>()

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

        @Option(names = ["-p", "--password"], description = ["Overwrite the default password for the signed file"])
        var password = "ReVanced"

        @Option(names = ["-d", "--deploy-on"], description = ["If specified, deploy to adb device with given name"])
        var deploy: String? = null

        @Option(names = ["-t", "--temp-dir"], description = ["Temporal resource cache directory"])
        var cacheDirectory = "revanced-cache"

        @Option(
            names = ["-c", "--clean"],
            description = ["Clean the temporal resource cache directory. This will be done anyways when running the patcher"]
        )
        var clean: Boolean = false
    }

    override fun run() {
        if (args.lArgs?.listOnly == true) {
            for (patchBundlePath in args.patchBundles) for (patch in JarPatchBundle(patchBundlePath).loadPatches()) {
                println("[available] ${patch.patchName}: ${patch.description}")
            }
            return
        }

        val args = args.pArgs ?: return

        val patcher = app.revanced.patcher.Patcher(
            PatcherOptions(
                args.inputFile, args.cacheDirectory, !args.disableResourcePatching
            )
        )

        val outputFile = File(args.outputPath)

        val adb: Adb? = args.deploy?.let {
            Adb(outputFile, patcher.data.packageMetadata.packageName, args.deploy!!, !args.mount)
        }

        val patchedFile = if (args.mount) outputFile
        else File(args.cacheDirectory).resolve("${outputFile.nameWithoutExtension}_raw.apk")

        Patcher.start(patcher, patchedFile)

        println("[aligning & signing]")

        if (!args.mount) {
            Signing.start(
                patchedFile,
                outputFile,
                args.cn,
                args.password,
            )
        }

        if (args.clean) File(args.cacheDirectory).deleteRecursively()

        adb?.let {
            println("[deploying]")
            it.deploy()
        }

        if (args.clean && args.deploy != null) Files.delete(outputFile.toPath())

        println("[done]")
    }
}
