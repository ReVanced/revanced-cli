package app.revanced.cli.signing

data class SigningOptions(
    val cn: String,
    val password: String,
    val keyStoreFilePath: String
)