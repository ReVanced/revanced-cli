package app.revanced.utils.dex

import lanchon.multidexlib2.BasicDexFileNamer
import org.jf.dexlib2.writer.io.MemoryDataStore
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files

val NAMER = BasicDexFileNamer()

object DexReplacer {
    fun replaceDex(source: File, dexFiles: Map<String, MemoryDataStore>) {
        FileSystems.newFileSystem(
            source.toPath(),
            null
        ).use { fs ->
            // Delete all classes?.dex files
            Files.walk(fs.rootDirectories.first()).forEach {
                if (
                    it.toString().endsWith(".dex") &&
                    NAMER.isValidName(it.fileName.toString())
                ) Files.delete(it)
            }
            // Write new dex files
            dexFiles
                .forEach { (dexName, dexData) ->
                    Files.write(fs.getPath("/$dexName"), dexData.data)
                }
        }
    }
}