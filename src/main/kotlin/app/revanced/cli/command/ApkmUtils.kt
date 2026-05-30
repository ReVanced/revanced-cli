package app.revanced.cli.command

import com.reandroid.apk.APKLogger
import com.reandroid.apk.ApkBundle
import com.reandroid.apk.ApkModule
import com.reandroid.app.AndroidManifest
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.logging.Level
import java.util.logging.Logger
import java.util.zip.ZipInputStream

internal fun File.isApkm() = extension.equals("apkm", ignoreCase = true)

internal data class ExtractedApkm(
    val extractedApksDirectory: File,
    val baseApk: File,
)

internal object ApkmUtils {
    private val logger = Logger.getLogger(ApkmUtils::class.java.name)

    private val splitInstallMetadataNames = setOf(
        "com.android.vending.splits.required",
        "com.android.vending.splits",
        "com.android.vending.derived.apk.id",
    )

    fun extract(
        apkmFile: File,
        temporaryFilesPath: File,
    ): ExtractedApkm {
        if (temporaryFilesPath.exists()) {
            temporaryFilesPath.deleteRecursively()
        }

        val extractedApksDirectory = temporaryFilesPath.resolve("extracted-apks").apply {
            mkdirs()
        }

        val apkFiles = apkmFile.extractApksTo(extractedApksDirectory)
        val baseApk = apkFiles.firstOrNull { it.name.equals("base.apk", ignoreCase = true) }
            ?: throw IOException("APKM file ${apkmFile.path} does not contain base.apk")

        return ExtractedApkm(extractedApksDirectory, baseApk)
    }

    fun mergeExtractedApks(
        extractedApkm: ExtractedApkm,
        mergedApk: File,
        patchedBaseApk: File,
    ): File {
        mergedApk.parentFile.mkdirs()
        if (mergedApk.exists()) {
            mergedApk.delete()
        }

        if (patchedBaseApk.canonicalFile != extractedApkm.baseApk.canonicalFile) {
            patchedBaseApk.copyTo(extractedApkm.baseApk, overwrite = true)
        }

        ApkBundle().use { bundle ->
            bundle.setAPKLogger(ArscLogger)
            bundle.loadApkDirectory(extractedApkm.extractedApksDirectory, true)

            bundle.mergeModules(true).use { mergedModule ->
                mergedModule.refreshTable()
                mergedModule.removeSplitInstallRequirements()
                mergedModule.refreshManifest()
                mergedModule.writeApk(mergedApk)
            }
        }

        logger.info("Merged APKM to $mergedApk")

        return mergedApk
    }

    private fun File.extractApksTo(outputDirectory: File): List<File> {
        val outputDirectoryPath = outputDirectory.toPath().toAbsolutePath().normalize()
        val apkFiles = mutableListOf<File>()

        ZipInputStream(inputStream().buffered()).use { zip ->
            while (true) {
                val entry = zip.nextEntry ?: break

                try {
                    if (entry.isDirectory || !entry.name.endsWith(".apk", ignoreCase = true)) {
                        continue
                    }

                    val outputFilePath = outputDirectoryPath.resolve(entry.name).normalize()
                    if (!outputFilePath.startsWith(outputDirectoryPath)) {
                        throw IOException("Unsafe APKM entry path: ${entry.name}")
                    }

                    Files.createDirectories(outputFilePath.parent)

                    if (Files.exists(outputFilePath)) {
                        throw IOException("Duplicate APKM entry path: ${entry.name}")
                    }

                    Files.copy(zip, outputFilePath)
                    apkFiles += outputFilePath.toFile()
                } finally {
                    zip.closeEntry()
                }
            }
        }

        if (apkFiles.isEmpty()) {
            throw IOException("APKM file $path does not contain any APK files")
        }

        logger.info("Extracted ${apkFiles.size} APK files from $path")

        return apkFiles
    }

    private fun ApkModule.removeSplitInstallRequirements() {
        val manifest = androidManifest ?: return
        val manifestElement = manifest.manifestElement ?: return

        manifest.setExtractNativeLibs(true)

        listOf(
            AndroidManifest.ID_isSplitRequired to AndroidManifest.NAME_isSplitRequired,
            AndroidManifest.ID_requiredSplitTypes to AndroidManifest.NAME_requiredSplitTypes,
            AndroidManifest.ID_splitTypes to AndroidManifest.NAME_splitTypes,
        ).forEach { (resourceId, name) ->
            manifestElement.removeAttributesWithId(resourceId)
            manifestElement.removeAttributesWithName(name)
        }

        manifest.getApplicationElement()?.removeElementsIf { element ->
            if (!element.equalsName(AndroidManifest.TAG_meta_data)) {
                return@removeElementsIf false
            }

            val name = element
                .searchAttributeByResourceId(AndroidManifest.ID_name)
                ?.getValueAsString()

            name in splitInstallMetadataNames
        }

        manifest.refreshFull()
    }

    private object ArscLogger : APKLogger {
        override fun logMessage(msg: String) = logger.info(msg)

        override fun logError(
            msg: String,
            tr: Throwable,
        ) = logger.log(Level.SEVERE, msg, tr)

        override fun logVerbose(msg: String) = logger.fine(msg)
    }
}
