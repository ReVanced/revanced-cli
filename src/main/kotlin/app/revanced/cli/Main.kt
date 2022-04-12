package app.revanced.cli

import app.revanced.cli.utils.PatchLoader
import app.revanced.cli.utils.Patches
import app.revanced.cli.utils.Preconditions
import app.revanced.cli.utils.SignatureParser
import app.revanced.patcher.Patcher
import app.revanced.patcher.patch.PatchResult
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import me.tongfei.progressbar.ProgressBarBuilder
import me.tongfei.progressbar.ProgressBarStyle
import java.io.File
import java.nio.file.Files

private const val CLI_NAME = "ReVanced CLI"
private val CLI_VERSION = Main::class.java.`package`.implementationVersion ?: "0.0.0-unknown"

class Main {
    companion object {
        private fun runCLI(
            inApk: String,
            inSignatures: String,
            inPatches: String,
            inIntegrations: String?,
            inOutput: String,
        ) {
            val bar = ProgressBarBuilder()
                .setTaskName("Working..")
                .setUpdateIntervalMillis(25)
                .continuousUpdate()
                .setStyle(ProgressBarStyle.ASCII)
                .build()
                .maxHint(1)
                .setExtraMessage("Initializing")
            val apk = Preconditions.isFile(inApk)
            val signatures = Preconditions.isFile(inSignatures)
            val patchesFile = Preconditions.isFile(inPatches)
            val output = Preconditions.isDirectory(inOutput)
            bar.step()

            val patcher = Patcher(
                apk,
                SignatureParser.parse(signatures.readText(), bar)
            )

            inIntegrations?.let {
                bar.reset().maxHint(1)
                    .extraMessage = "Merging integrations"
                val integrations = Preconditions.isFile(it)
                patcher.addFiles(integrations)
                bar.step()
            }

            bar.reset().maxHint(1)
                .extraMessage = "Loading patches"
            PatchLoader.injectPatches(patchesFile)
            bar.step()

            val patches = Patches.loadPatches().map { it() }
            patcher.addPatches(patches)

            val amount = patches.size.toLong()
            bar.reset().maxHint(amount)
                .extraMessage = "Applying patches"
            val results = patcher.applyPatches {
                bar.step().extraMessage = "Applying $it"
            }

            bar.reset().maxHint(-1)
                .extraMessage = "Generating dex files"
            val dexFiles = patcher.save()
            bar.reset().maxHint(dexFiles.size.toLong())
                .extraMessage = "Saving dex files"
            dexFiles.forEach { (dexName, dexData) ->
                Files.write(File(output, dexName).toPath(), dexData.data)
                bar.step()
            }
            bar.close()

            println("All done!")
            printResults(results)
        }

        private fun printResults(results: Map<String, Result<PatchResult>>) {
            for ((name, result) in results) {
                if (result.isSuccess) {
                    println("Patch $name was applied successfully!")
                } else {
                    println("Patch $name failed to apply! Cause:")
                    result.exceptionOrNull()!!.printStackTrace()
                }
            }
        }

        @JvmStatic
        fun main(args: Array<String>) {
            println("$CLI_NAME version $CLI_VERSION")
            val parser = ArgParser(CLI_NAME)

            val apk by parser.option(
                ArgType.String,
                fullName = "apk",
                shortName = "a",
                description = "APK file"
            ).required()
            val signatures by parser.option(
                ArgType.String,
                fullName = "signatures",
                shortName = "s",
                description = "Signatures JSON file"
            ).required()
            val patches by parser.option(
                ArgType.String,
                fullName = "patches",
                shortName = "p",
                description = "Patches JAR file"
            ).required()
            val integrations by parser.option(
                ArgType.String,
                fullName = "integrations",
                shortName = "i",
                description = "Integrations APK file"
            )
            val output by parser.option(
                ArgType.String,
                fullName = "output",
                shortName = "o",
                description = "Output directory"
            ).required()

            parser.parse(args)
            runCLI(
                apk,
                signatures,
                patches,
                integrations,
                output,
            )
        }
    }
}