package app.revanced.cli

import app.revanced.patch.PatchLoader
import app.revanced.patch.Patches
import picocli.CommandLine
import picocli.CommandLine.*
import java.io.File

@Command(
    name = "ReVanced-CLI", version = ["1.0.0"], mixinStandardHelpOptions = true
)
internal object MainCommand : Runnable {
    @Option(names = ["-p", "--patches"], description = ["One or more bundles of patches"])
    internal var patchBundles = arrayOf<File>()

    @Parameters(
        paramLabel = "INCLUDE",
        description = ["Which patches to include. If none is specified, all compatible patches will be included"]
    )
    internal var includedPatches = arrayOf<String>()

    @Option(names = ["-c", "--cache"], description = ["Output resource cache directory"], required = true)
    internal lateinit var cacheDirectory: String

    @Option(names = ["-r", "--resource-patcher"], description = ["Enable patching resources"])
    internal var patchResources: Boolean = false

    @Option(names = ["-w", "--wipe-after"], description = ["Wipe the temporal directory before exiting the patcher"])
    internal var wipe: Boolean = false


    @Option(names = ["-l", "--list"], description = ["List patches only"])
    internal var listOnly: Boolean = false

    @Option(names = ["-m", "--merge"], description = ["One or more dex file containers to merge"])
    internal var mergeFiles = listOf<File>()

    @Option(names = ["-a", "--apk"], description = ["Input file to be patched"], required = true)
    internal lateinit var inputFile: File

    @Option(names = ["-o", "--out"], description = ["Output file path"], required = true)
    internal lateinit var outputPath: String

    override fun run() {
        if (listOnly) {
            patchBundles.forEach {
                PatchLoader.injectPatches(it)
                Patches.loadPatches().forEach {
                    println(it().metadata)
                }
            }
            return
        }

        Patcher.run()

        if (!wipe) return
        File(cacheDirectory).deleteRecursively()
    }
}

internal fun main(args: Array<String>) {
    CommandLine(MainCommand).execute(*args)
}