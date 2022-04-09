package app.revanced.cli

import app.revanced.cli.utils.SignatureParser
import app.revanced.patcher.Patcher
import app.revanced.patches.Index.patches
import java.io.File

fun main(args: Array<String>) {
    val patcher = Patcher(
        File(args[0]), // in.apk
        File(args[1]), // out path
        SignatureParser.parse(args[2]).toTypedArray() // signatures.json
    )

    // add integrations dex container
    patcher.addFiles(File(args[3]))

    // load all patches
    for (patch in patches) {
        patcher.addPatches(patch())
    }

    patcher.applyPatches().forEach{ (name, result) ->
        println("$name: $result")
    }

    // save patched apk
    patcher.save()
}