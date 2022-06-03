package app.revanced.cli

import app.revanced.patcher.PatcherOptions
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.util.patch.implementation.JarPatchBundle
import app.revanced.utils.adb.Adb
import app.revanced.utils.patcher.addPatchesFiltered
import app.revanced.utils.signature.Signature
import picocli.CommandLine.*
import java.io.File

@Command(
    name = "ReVanced-CLI", version = ["1.0.0"], mixinStandardHelpOptions = true
)
internal object MainCommand : Runnable {
    @Parameters(
        paramLabel = "INCLUDE",
        description = ["Which patches to include. If none is specified, all compatible patches will be included"]
    )
    internal var includedPatches = arrayOf<String>()

    @Option(names = ["-p", "--patches"], description = ["One or more bundles of patches"])
    internal var patchBundles = arrayOf<String>()

    @Option(names = ["-t", "--temp-dir"], description = ["Temporal resource cache directory"])
    internal var cacheDirectory = "revanced-cache"

    @Option(names = ["-r", "--resource-patcher"], description = ["Disable patching resources"])
    internal var disableResourcePatching: Boolean = false

    @Option(
        names = ["-c", "--clean"],
        description = ["Clean the temporal resource cache directory. This will be done anyways when running the patcher"]
    )
    internal var clean: Boolean = false

    @Option(names = ["-l", "--list"], description = ["List patches only"])
    internal var listOnly: Boolean = false

    @Option(names = ["-s", "--signature-checker"], description = ["Check signatures of all patches"])
    internal var signatureCheck: Boolean = false

    @Option(names = ["-m", "--merge"], description = ["One or more dex file containers to merge"])
    internal var mergeFiles = listOf<File>()

    @Option(names = ["-a", "--apk"], description = ["Input file to be patched"], required = true)
    internal lateinit var inputFile: File

    @Option(names = ["-o", "--out"], description = ["Output file path"], required = true)
    internal lateinit var outputPath: String

    @Option(names = ["-d", "--deploy-on"], description = ["If specified, deploy to adb device with given name"])
    internal var deploy: String? = null

    override fun run() {
        if (listOnly) {
            for (patchBundlePath in patchBundles) for (it in JarPatchBundle(patchBundlePath).loadPatches()) {

                // TODO: adjust extension methods to be able to do this
                val name = (it.annotations.find { it is Name } as? Name)?.name ?: it.simpleName
                println(
                    "[available] $name"
                )
            }
            return
        }

        val patcher = app.revanced.patcher.Patcher(PatcherOptions(inputFile, cacheDirectory, !disableResourcePatching))

        if (signatureCheck) {
            patcher.addPatchesFiltered()
            Signature.checkSignatures(patcher)
            return
        }

        val outputFile = File(outputPath)

        var adb: Adb? = null
        deploy?.let {
            adb = Adb(
                outputFile, patcher.packageName, deploy!!
            )
        }

        Patcher.start(patcher)

        if (clean) File(cacheDirectory).deleteRecursively()

        adb?.deploy()

        if (clean) outputFile.delete()
    }
}
