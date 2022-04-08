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

    // load all patches
    for (patch in patches) {
        patcher.addPatches(patch())
    }

    // apply all patches
    patcher.applyPatches().forEach{ (name, result) ->
        println("$name: $result")
    }

    // save patched apk
    patcher.save()
}