package app.revanced.utils.filesystem

import java.io.Closeable
import java.io.File
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files

internal class FileSystemUtils(
    file: File
) : Closeable {
    private var fileSystem: FileSystem

    init {
        fileSystem = FileSystems.newFileSystem(file.toPath(), null as ClassLoader?)
    }

    private fun deleteDirectory(dirPath: String) {
        val files = Files.walk(fileSystem.getPath("$dirPath/"))

        files
            .sorted(Comparator.reverseOrder())
            .forEach {

                Files.delete(it)
            }

        files.close()
    }


    internal fun replaceDirectory(replacement: File) {
        if (!replacement.isDirectory) throw Exception("${replacement.name} is not a directory.")

        // FIXME: make this delete the directory recursively
        //deleteDirectory(replacement.name)
        //val path = Files.createDirectory(fileSystem.getPath(replacement.name))

        val excludeFromPath = replacement.path.removeSuffix(replacement.name)
        for (path in Files.walk(replacement.toPath())) {
            val file = path.toFile()
            if (file.isDirectory) {
                val relativePath = path.toString().removePrefix(excludeFromPath)
                val fileSystemPath = fileSystem.getPath(relativePath)
                if (!Files.exists(fileSystemPath)) Files.createDirectory(fileSystemPath)

                continue
            }

            replaceFile(path.toString().removePrefix(excludeFromPath), file.readBytes())
        }
    }

    internal fun replaceFile(sourceFile: String, content: ByteArray) {
        val path = fileSystem.getPath(sourceFile)
        Files.deleteIfExists(path)
        Files.write(path, content)
    }

    override fun close() {
        fileSystem.close()
    }
}