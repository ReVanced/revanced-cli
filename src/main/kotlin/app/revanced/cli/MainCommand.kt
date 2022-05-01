package app.revanced.cli

import app.revanced.cli.MainCommand.excludedPatches
import app.revanced.cli.MainCommand.patchBundles
import app.revanced.patcher.Patcher
import app.revanced.patcher.patch.Patch
import app.revanced.utils.dex.DexReplacer
import app.revanced.utils.patch.PatchLoader
import app.revanced.utils.patch.Patches
import app.revanced.utils.signing.Signer
import picocli.CommandLine
import picocli.CommandLine.*
import java.io.File

@Command(
    name = "ReVanced-CLI",
    version = ["1.0.0"],
    mixinStandardHelpOptions = true
)
object MainCommand : Runnable {
    @Option(names = ["-p", "--patches"], description = ["One or more bundles of patches"])
    var patchBundles = arrayOf<File>()

    @Parameters(paramLabel = "EXCLUDE", description = ["Which patches to exclude"])
    var excludedPatches = arrayOf<String>()

    @Option(names = ["-l", "--list"], description = ["List patches only"])
    var listOnly: Boolean = false

    @Option(names = ["-m", "--merge"], description = ["One or more dex file containers to merge"])
    var mergeFiles = listOf<File>()

    @Option(names = ["-a", "--apk"], description = ["Input file to be patched"], required = true)
    lateinit var inputFile: File

    @Option(names = ["-o", "--out"], description = ["Output file path"], required = true)
    lateinit var outputPath: String

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

        val patcher = Patcher(inputFile)
        // merge files like necessary integrations
        patcher.addFiles(mergeFiles)
        // add patches, but filter incompatible or excluded patches
        patcher.addPatchesFiltered()
        // apply patches
        for (patchResult in patcher.applyPatches {
            println("Applying: $it")
        }) {
            println(patchResult)
        }

        // write output file
        val outFile = File(outputPath)
        inputFile.copyTo(outFile)
        DexReplacer.replaceDex(outFile, patcher.save())

        // sign the apk file
        Signer.signApk(outFile)
    }
}

private fun Patcher.addPatchesFiltered() {
    // TODO: get package metadata (outside of this method) for apk file which needs to be patched
    val packageName = "com.example.exampleApp"
    val packageVersion = "1.2.3"

    patchBundles.forEach { bundle ->
        PatchLoader.injectPatches(bundle)
        val includedPatches = mutableListOf<Patch>()
        Patches.loadPatches().forEach patch@{
            val patch = it()

            // TODO: filter out incompatible patches with package metadata
            val filterOutPatches = true
            if (filterOutPatches &&
                !patch.metadata.compatiblePackages.any { packageMetadata ->
                    packageMetadata.name == packageName &&
                            packageMetadata.versions.any {
                                it == packageVersion
                            }
                }
            ) {
                // TODO: report to stdout
                return@patch
            }

            if (excludedPatches.contains(patch.metadata.shortName)) {
                // TODO: report to stdout
                return@patch
            }

            includedPatches.add(patch)
        }
        this.addPatches(includedPatches)
    }
}

fun main(args: Array<String>) {
    CommandLine(MainCommand).execute(*args)
}