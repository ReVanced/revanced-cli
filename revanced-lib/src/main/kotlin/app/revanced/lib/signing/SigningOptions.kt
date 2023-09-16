package app.revanced.lib.signing

import java.io.File

data class SigningOptions(
    val commonName: String,
    val password: String,
    val keyStoreOutputFilePath: File
)