package app.revanced.cli

import app.revanced.cli.utils.PatchLoader
import app.revanced.cli.utils.Patches
import app.revanced.cli.utils.Preconditions
import app.revanced.cli.utils.SignatureParser
import app.revanced.patcher.Patcher
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
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
            inOutput: String,
        ) {
            val apk = Preconditions.isFile(inApk)
            val signatures = Preconditions.isFile(inSignatures)
            val patchesFile = Preconditions.isFile(inPatches)
            val output = Preconditions.isDirectory(inOutput)

            val patcher = Patcher(
                apk,
                SignatureParser
                    .parse(signatures.readText())
                    .toTypedArray()
            )

            PatchLoader.injectPatches(patchesFile)
            val patches = Patches.loadPatches()
            patcher.addPatches(*patches.map { it() }.toTypedArray())

            val results = patcher.applyPatches()
            for ((name, result) in results) {
                println("$name: $result")
            }

            val dexFiles = patcher.save()
            dexFiles.forEach { (dexName, dexData) ->
                Files.write(File(output, dexName).toPath(), dexData.buffer)
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
            val output by parser.option(
                ArgType.String,
                fullName = "output",
                shortName = "o",
                description = "Output directory"
            ).required()
            // TODO: merge dex file

            parser.parse(args)
            runCLI(
                apk,
                signatures,
                patches,
                output,
            )
        }
    }
}