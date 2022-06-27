package app.revanced.utils.filesystem

import java.io.Closeable
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry

internal class ZipFileSystemUtils(
    file: File
) : Closeable {
    private var zipFileSystem = FileSystems.newFileSystem(file.toPath(), mapOf("noCompression" to true))

    private fun Path.deleteRecursively() {
        if (!Files.exists(this)) {
            throw IllegalStateException("File exists in real folder but not in zip file system")
        }

        if (Files.isDirectory(this)) {
            Files.list(this).forEach { path ->
                path.deleteRecursively()
            }
        }

        Files.delete(this)
    }

    internal fun getFile(path: String) = zipFileSystem.getPath(path)

    internal fun writePathRecursively(path: Path) {
        Files.list(path).use { fileStream ->
            fileStream.forEach { filePath ->
                val fileSystemPath = filePath.getRelativePath(path)
                fileSystemPath.deleteRecursively()
            }
        }

        Files.walk(path).use { fileStream ->
            // don't include build directory
            // by skipping the root node.
            fileStream.skip(1).forEach { filePath ->
                val relativePath = filePath.getRelativePath(path)

                if (Files.isDirectory(filePath)) {
                    Files.createDirectory(relativePath)
                    return@forEach
                }

                Files.copy(filePath, relativePath)
            }
        }
    }

    internal fun write(path: String, content: ByteArray) = Files.write(zipFileSystem.getPath(path), content)

    private fun Path.getRelativePath(path: Path): Path = zipFileSystem.getPath(path.relativize(this).toString())

    internal fun uncompress(vararg paths: String) =
        paths.forEach { Files.setAttribute(zipFileSystem.getPath(it), "zip:method", ZipEntry.STORED) }

    override fun close() = zipFileSystem.close()
}