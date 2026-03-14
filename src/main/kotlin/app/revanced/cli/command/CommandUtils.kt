package app.revanced.cli.command

import app.revanced.library.getPublicKeyRingCollection
import app.revanced.library.getSignature
import app.revanced.library.matchGitHub
import app.revanced.library.verifyProvenance
import app.revanced.library.verifySignature
import app.revanced.patcher.patch.Patches
import dev.sigstore.fulcio.client.ImmutableFulcioCertificateMatcher.Builder
import picocli.CommandLine
import java.io.File
import java.util.logging.Logger

class PatchesFileInput {

    @CommandLine.Option(
        names = ["-p", "--patches"],
        description = ["One or more path to RVP files."],
        required = true
    )
    lateinit var patchesFile: File

    @CommandLine.ArgGroup(multiplicity = "1", exclusive = true)
    lateinit var signatureVerification: SignatureVerification

    class SignatureVerification {
        @CommandLine.Option(
            names = ["-b", "--bypass-verification"],
            description = ["Bypass signature and build provenance verification for this RVP file."],
            required = true,
        )
        var bypass: Boolean = false

        @CommandLine.ArgGroup(exclusive = false)
        var options: SignatureVerificationOptions? = null

        class SignatureVerificationOptions {
            @CommandLine.Option(
                names = ["-s", "--signature"],
                description = ["Path to the PGP signature file for this RVP file."],
                required = true,
            )
            lateinit var signatureFile: File

            @CommandLine.Option(
                names = ["-k", "--public-key-ring"],
                description = ["Path to the PGP public key ring for this RVP file."],
                required = true,
            )
            lateinit var publicKeyRingFile: File

            @CommandLine.Option(
                names = ["-a", "--attestation"],
                description = ["Path to the build provenance attestation file for this RVP file."],
                required = true,
            )
            lateinit var attestationFile: File

            @CommandLine.ArgGroup(exclusive = true, multiplicity = "1")
            lateinit var provenance: Provenance

            class Provenance {
                @CommandLine.ArgGroup(exclusive = false)
                var github: GitHubOptions? = null

                class GitHubOptions {
                    @CommandLine.Option(
                        names = ["-r", "--repository"],
                        description = ["GitHub repository in the format 'owner/repo'."],
                        required = true,
                    )
                    lateinit var repository: String
                }
            }
        }
    }

    fun isValid(): Boolean {
        if (!patchesFile.exists()) {
            logger.severe("Patches file ${patchesFile.path} does not exist")
            return false
        }

        if (!signatureVerification.bypass) {
            val options = signatureVerification.options!!

            // Signature verification.
            if (!options.signatureFile.exists()) {
                logger.severe("Signature file ${options.signatureFile.path} does not exist")
                return false
            }

            if (!options.publicKeyRingFile.exists()) {
                logger.severe("Public key ring file ${options.publicKeyRingFile.path} does not exist")
                return false
            }

            val signature = options.signatureFile.inputStream().use { getSignature(it) }
            val publicKey = options.publicKeyRingFile.inputStream()
                .use { getPublicKeyRingCollection(it) }
                .getPublicKey(signature.keyID)

            if (!verifySignature(patchesFile.readBytes(), signature, publicKey)) {
                logger.severe("Signature verification failed for ${patchesFile.path}")
                return false
            }

            // Provenance verification.
            if (!options.attestationFile.exists()) {
                logger.severe("Attestation file ${options.attestationFile.path} does not exist")
                return false
            }

            val buildMatcher: Builder.() -> Builder = when {
                options.provenance.github != null ->
                    fun Builder.(): Builder {
                        return matchGitHub(options.provenance.github!!.repository)
                    }

                else -> {
                    logger.severe("No provenance options specified for ${patchesFile.path}")
                    return false
                }
            }

            val isValid = options.attestationFile.inputStream().use { attestationStream ->
                verifyProvenance(patchesFile.readBytes(), attestationStream, buildMatcher)
            }

            if (!isValid) {
                logger.severe("Provenance verification failed for ${patchesFile.path}")
                return false
            }

        }

        return true
    }

    companion object {
        private val logger = Logger.getLogger(PatchesFileInput::class.java.name)

        fun loadPatches(patchesFiles: List<PatchesFileInput>): Patches? {
            if (!patchesFiles.all(PatchesFileInput::isValid)) return null

            return app.revanced.patcher.patch.loadPatches(
                patchesFiles = patchesFiles.map { it.patchesFile }.toTypedArray()
            ) { file, throwable ->
                logger.severe("Failed to load patches from ${file.path}:\n${throwable.stackTraceToString()}")
            }
        }
    }

}

class OptionKeyConverter : CommandLine.ITypeConverter<String> {
    override fun convert(value: String): String = value
}

class OptionValueConverter : CommandLine.ITypeConverter<Any?> {
    override fun convert(value: String?): Any? {
        value ?: return null

        return when {
            value.startsWith("[") && value.endsWith("]") -> {
                val innerValue = value.substring(1, value.length - 1)

                buildList {
                    var nestLevel = 0
                    var insideQuote = false
                    var escaped = false

                    val item = buildString {
                        for (char in innerValue) {
                            when (char) {
                                '\\' -> {
                                    if (escaped || nestLevel != 0) {
                                        append(char)
                                    }

                                    escaped = !escaped
                                }

                                '"', '\'' -> {
                                    if (!escaped) {
                                        insideQuote = !insideQuote
                                    } else {
                                        escaped = false
                                    }

                                    append(char)
                                }

                                '[' -> {
                                    if (!insideQuote) {
                                        nestLevel++
                                    }

                                    append(char)
                                }

                                ']' -> {
                                    if (!insideQuote) {
                                        nestLevel--

                                        if (nestLevel == -1) {
                                            return value
                                        }
                                    }

                                    append(char)
                                }

                                ',' -> if (nestLevel == 0) {
                                    if (insideQuote) {
                                        append(char)
                                    } else {
                                        add(convert(toString()))
                                        setLength(0)
                                    }
                                } else {
                                    append(char)
                                }

                                else -> append(char)
                            }
                        }
                    }

                    if (item.isNotEmpty()) {
                        add(convert(item))
                    }
                }
            }

            value.startsWith("\"") && value.endsWith("\"") -> value.substring(1, value.length - 1)
            value.startsWith("'") && value.endsWith("'") -> value.substring(1, value.length - 1)
            value.endsWith("f") -> value.dropLast(1).toFloat()
            value.endsWith("L") -> value.dropLast(1).toLong()
            value.equals("true", ignoreCase = true) -> true
            value.equals("false", ignoreCase = true) -> false
            value.toIntOrNull() != null -> value.toInt()
            value.toLongOrNull() != null -> value.toLong()
            value.toDoubleOrNull() != null -> value.toDouble()
            value.toFloatOrNull() != null -> value.toFloat()
            value == "null" -> null
            value == "int[]" -> emptyList<Int>()
            value == "long[]" -> emptyList<Long>()
            value == "double[]" -> emptyList<Double>()
            value == "float[]" -> emptyList<Float>()
            value == "boolean[]" -> emptyList<Boolean>()
            value == "string[]" -> emptyList<String>()
            else -> value
        }
    }
}
