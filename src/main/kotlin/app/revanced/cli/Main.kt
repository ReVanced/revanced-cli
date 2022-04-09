package app.revanced.cli

import app.revanced.cli.utils.SignatureParser
import app.revanced.patcher.Patcher
import app.revanced.patches.Index.patches
import org.jf.dexlib2.writer.io.MemoryDataStore
import java.io.File
import java.nio.file.Files

fun main(args: Array<String>) {
    val patcher = Patcher(
        File(args[0]), // in.apk
        SignatureParser.parse(args[2]).toTypedArray() // signatures.json
    )

    // add integrations dex container
    patcher.addFiles(File(args[3]))

    for (patch in patches) {
        patcher.addPatches(patch())
    }

    patcher.applyPatches().forEach { (name, result) ->
        println("$name: $result")
    }

    // save patched apk
    val dexFiles: Map<String, MemoryDataStore> = patcher.save()
    dexFiles.forEach { (t, p) ->
        Files.write(File(args[1], t).toPath(), p.buffer)
    }
}