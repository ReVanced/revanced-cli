package app.revanced.lib

import app.revanced.lib.signing.ApkSigner
import app.revanced.lib.signing.ApkSigner.signApk
import app.revanced.lib.zip.ZipFile
import app.revanced.lib.zip.structures.ZipEntry
import app.revanced.patcher.PatcherResult
import java.io.File
import java.util.logging.Logger
import kotlin.io.path.deleteIfExists

@Suppress("MemberVisibilityCanBePrivate", "unused")
object ApkUtils {
    private val logger = Logger.getLogger(ApkUtils::class.java.name)

    /**
     * Creates a new apk from [apkFile] and [patchedEntriesSource] and writes it to [outputFile].
     *
     * @param apkFile The apk to copy entries from.
     * @param outputFile The apk to write the new entries to.
     * @param patchedEntriesSource The result of the patcher to add the patched dex files and resources.
     */
    fun copyAligned(apkFile: File, outputFile: File, patchedEntriesSource: PatcherResult) {
        logger.info("Aligning ${apkFile.name}")

        outputFile.toPath().deleteIfExists()

        ZipFile(outputFile).use { file ->
            patchedEntriesSource.dexFiles.forEach {
                file.addEntryCompressData(
                    ZipEntry(it.name), it.stream.readBytes()
                )
            }

            patchedEntriesSource.resourceFile?.let {
                file.copyEntriesFromFileAligned(
                    ZipFile(it), ZipFile.apkZipEntryAlignment
                )
            }

            // TODO: Do not compress result.doNotCompress

            // TODO: Fix copying resources that are not needed anymore.
            file.copyEntriesFromFileAligned(
                ZipFile(apkFile), ZipFile.apkZipEntryAlignment
            )
        }
    }

    /**
     * Signs the [apk] file and writes it to [output].
     *
     * @param apk The apk to sign.
     * @param output The apk to write the signed apk to.
     * @param signingOptions The options to use for signing.
     */
    fun sign(
        apk: File,
        output: File,
        signingOptions: SigningOptions,
    ) {
        // Get the keystore from the file or create a new one.
        val keyStore = if (signingOptions.keyStore.exists()) {
            ApkSigner.readKeyStore(signingOptions.keyStore.inputStream(), signingOptions.keyStorePassword)
        } else {
            val entry = ApkSigner.KeyStoreEntry(signingOptions.alias, signingOptions.password)

            // Create a new keystore with a new keypair and saves it.
            ApkSigner.newKeyStore(listOf(entry)).also { keyStore ->
                keyStore.store(
                    signingOptions.keyStore.outputStream(),
                    signingOptions.keyStorePassword?.toCharArray()
                )
            }
        }

        ApkSigner.newApkSignerBuilder(
            keyStore,
            signingOptions.alias,
            signingOptions.password,
            signingOptions.signer,
            signingOptions.signer
        ).signApk(apk, output)
    }

    /**
     * Options for signing an apk.
     *
     * @param keyStore The keystore to use for signing.
     * @param keyStorePassword The password for the keystore.
     * @param alias The alias of the key store entry to use for signing.
     * @param password The password for recovering the signing key.
     * @param signer The name of the signer.
     */
    class SigningOptions(
        val keyStore: File,
        val keyStorePassword: String?,
        val alias: String = "ReVanced Key",
        val password: String = "",
        val signer: String = "ReVanced",
    )
}