package app.revanced.cli.utils.signature

import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

object SignatureDownloader {

    fun download(out: String) {
        val url =
            URL("https://raw.githubusercontent.com/ReVancedTeam/revanced-signatures/main/signatures/youtube.signatures.json")

        val file = File(out)

        if (!file.exists()) {
            url.openStream().use { Files.copy(it, Paths.get(out)) }
        }

    }

}