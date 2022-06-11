package app.revanced.cli.command

import app.revanced.cli.patcher.Patcher
import app.revanced.cli.signing.Signing
import app.revanced.patcher.PatcherOptions
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.util.patch.implementation.JarPatchBundle
import app.revanced.utils.adb.Adb
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File
import java.nio.file.Files

@Command(
    name = "ReVanced-CLI", version = ["1.0.0"], mixinStandardHelpOptions = true,
)
internal object MainCommand : Runnable {
    @Option(names = ["-a", "--apk"], description = ["Input file to be patched"], required = true)
    lateinit var inputFile: File

    @Option(names = ["-o", "--out"], description = ["Output file path"], required = true)
    lateinit var outputPath: String

    @Option(
        names = ["-i", "--include"],
        description = ["Which patches to include. If none is specified, all compatible default patches will be included"]
    )
    var includedPatches = arrayOf<String>()

    @Option(names = ["-r", "--resource-patcher"], description = ["Disable patching resources"])
    var disableResourcePatching: Boolean = false

    @Option(names = ["--debugging"], description = ["Disable patch version compatibility"])
    var debugging: Boolean = false

    @Option(names = ["-m", "--merge"], description = ["One or more dex file containers to merge"])
    var mergeFiles = listOf<File>()

    @Option(names = ["-b", "--bundles"], description = ["One or more bundles of patches"])
    var patchBundles = arrayOf<String>()

    @Option(names = ["-l", "--list"], description = ["List patches only"])
    var listOnly: Boolean = false

    @Option(names = ["--install"], description = ["If specified, instead of mounting, install"])
    var install: Boolean = false

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

    override fun run() {
        if (listOnly) {
            for (patchBundlePath in patchBundles) for (patch in JarPatchBundle(patchBundlePath).loadPatches()) {
                println("[available] ${patch.patchName}")
            }
            return
        }

        val patcher = app.revanced.patcher.Patcher(PatcherOptions(inputFile, cacheDirectory, !disableResourcePatching))

        val outputFile = File(outputPath)

        val adb: Adb? = deploy?.let {
            Adb(outputFile, patcher.data.packageMetadata.packageName, deploy!!, install)
        }

        val patchedFile = if (install) File(cacheDirectory).resolve("raw.apk") else outputFile

        Patcher.start(patcher, patchedFile)

        if (install) {
            Signing.start(
                patchedFile,
                outputFile,
                cn,
                password,
            )
        }

        if (clean) File(cacheDirectory).deleteRecursively()

        adb?.let {
            println("[deploying]")
            it.deploy()
        }

        if (clean && deploy != null) Files.delete(outputFile.toPath())

        println("[done]")
    }
}
