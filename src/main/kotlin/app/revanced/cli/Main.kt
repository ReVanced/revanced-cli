package app.revanced.cli

import app.revanced.cli.runner.AdbRunner
import app.revanced.cli.utils.PatchLoader
import app.revanced.cli.utils.Patches
import app.revanced.cli.utils.Preconditions
import app.revanced.patcher.Patcher
import app.revanced.patcher.patch.PatchMetadata
import app.revanced.patcher.patch.PatchResult
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
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
            inPatches: String,
            inIntegrations: String?,
            inOutput: String,
            inRunOnAdb: String?,
            hideResults: Boolean,
            noLogging: Boolean,
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
            val patchesFile = Preconditions.isFile(inPatches)
            val output = Preconditions.isDirectory(inOutput)
            bar.step()

            val patcher = Patcher(apk)

            inIntegrations?.let {
                bar.reset().maxHint(1)
                    .extraMessage = "Merging integrations"
                val integrations = Preconditions.isFile(it)
                patcher.addFiles(listOf(integrations))
                bar.step()
            }

            bar.reset().maxHint(1)
                .extraMessage = "Loading patches"
            PatchLoader.injectPatches(patchesFile)
            val patches = Patches.loadPatches().map { it() }
            patcher.addPatches(patches)
            bar.step()

            bar.reset().maxHint(1)
                .extraMessage = "Resolving signatures"
            patcher.resolveSignatures()
            bar.step()

            val szPatches = patches.size.toLong()
            bar.reset().maxHint(szPatches)
                .extraMessage = "Applying patches"
            val results = patcher.applyPatches {
                bar.step().extraMessage = "Applying $it"
            }

            bar.reset().maxHint(-1)
                .extraMessage = "Generating dex files"
            val dexFiles = patcher.save()

            val szDexFiles = dexFiles.size.toLong()
            bar.reset().maxHint(szDexFiles)
                .extraMessage = "Saving dex files"
            dexFiles.forEach { (dexName, dexData) ->
                Files.write(File(output, dexName).toPath(), dexData.data)
                bar.step()
            }
            bar.stepTo(szDexFiles)

            bar.close()

            inRunOnAdb?.let { device ->
                AdbRunner.runApk(
                    apk,
                    dexFiles,
                    output,
                    device,
                    noLogging
                )
            }

            println("All done!")
            if (!hideResults) {
                printResults(results)
            }
        }

        private fun printResults(results: Map<PatchMetadata, Result<PatchResult>>) {
            for ((metadata, result) in results) {
                if (result.isSuccess) {
                    println("${metadata.shortName} was applied successfully!")
                } else {
                    println("${metadata.shortName} failed to apply! Cause:")
                    result.exceptionOrNull()!!.printStackTrace()
                }
            }
        }

        @JvmStatic
        fun main(args: Array<String>) {
            println("$CLI_NAME version $CLI_VERSION")
            val parser = ArgParser(CLI_NAME)

            // TODO: add some kind of incremental building, so merging integrations can be skipped.
            // this can be achieved manually, but doing it automatically is better.

            val apk by parser.option(
                ArgType.String,
                fullName = "apk",
                shortName = "a",
                description = "APK file"
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
            val runOnAdb by parser.option(
                ArgType.String,
                fullName = "run-on",
                description = "After the CLI is done building, which ADB device should it run on?"
            )
            // TODO: package name
            val hideResults by parser.option(
                ArgType.Boolean,
                fullName = "hide-results",
                description = "Don't print the patch results."
            ).default(false)
            val noLogging by parser.option(
                ArgType.Boolean,
                fullName = "no-logging",
                description = "Don't print the output of the application when used in combination with \"run-on\"."
            ).default(false)

            parser.parse(args)
            runCLI(
                apk,
                patches,
                integrations,
                output,
                runOnAdb,
                hideResults,
                noLogging,
            )
        }
    }
}