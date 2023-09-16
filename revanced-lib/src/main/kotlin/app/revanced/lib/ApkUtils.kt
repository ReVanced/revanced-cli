package app.revanced.lib

import app.revanced.lib.signing.ApkSigner
import app.revanced.lib.signing.SigningOptions
import app.revanced.lib.zip.ZipAligner
import app.revanced.lib.zip.ZipFile
import app.revanced.lib.zip.structures.ZipEntry
import app.revanced.patcher.PatcherResult
import java.io.File
import java.util.logging.Logger

object ApkUtils {
    private val logger = Logger.getLogger(ApkUtils::class.java.name)

    /**
     * Aligns and signs the apk at [apkFile] and writes it to [outputFile].
     *
     * @param apkFile The apk to align and sign.
     * @param outputFile The apk to write the aligned and signed apk to.
     * @param signingOptions The options to use for signing.
     * @param patchedEntriesSource The result of the patcher to add the patched dex files and resources.
     */
    fun alignAndSign(
        apkFile: File,
        outputFile: File,
        signingOptions: SigningOptions,
        patchedEntriesSource: PatcherResult
    ) {
        if (outputFile.exists()) outputFile.delete()

        align(apkFile, outputFile, patchedEntriesSource)
        sign(outputFile, outputFile, signingOptions)
    }

    /**
     * Creates a new apk from [apkFile] and [patchedEntriesSource] and writes it to [outputFile].
     *
     * @param apkFile The apk to copy entries from.
     * @param outputFile The apk to write the new entries to.
     * @param patchedEntriesSource The result of the patcher to add the patched dex files and resources.
     */
    private fun align(apkFile: File, outputFile: File, patchedEntriesSource: PatcherResult) {
        logger.info("Aligning ${apkFile.name}")

        ZipFile(outputFile).use { file ->
            patchedEntriesSource.dexFiles.forEach {
                file.addEntryCompressData(
                    ZipEntry(it.name), it.stream.readBytes()
                )
            }

            patchedEntriesSource.resourceFile?.let {
                file.copyEntriesFromFileAligned(
                    ZipFile(it), ZipAligner::getEntryAlignment
                )
            }

            // TODO: Do not compress result.doNotCompress

            // TODO: Fix copying resources that are not needed anymore.
            file.copyEntriesFromFileAligned(
                ZipFile(apkFile), ZipAligner::getEntryAlignment
            )
        }
    }


    /**
     * Signs the apk at [apk] and writes it to [output].
     *
     * @param apk The apk to sign.
     * @param output The apk to write the signed apk to.
     * @param signingOptions The options to use for signing.
     */
    private fun sign(
        apk: File,
        output: File,
        signingOptions: SigningOptions,
    ) {
        logger.info("Signing ${apk.name}")

        ApkSigner(signingOptions).signApk(apk, output)
    }
}