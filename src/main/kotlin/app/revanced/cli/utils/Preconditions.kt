package app.revanced.cli.utils

import java.io.File
import java.io.FileNotFoundException

class Preconditions {
    companion object {
        fun isFile(path: String): File {
            val f = File(path)
            if (!f.exists()) {
                throw FileNotFoundException(f.toString())
            }
            return f
        }

        fun isDirectory(path: String): File {
            val f = isFile(path)
            if (!f.isDirectory) {
                throw IllegalArgumentException("$f is not a directory")
            }
            return f
        }
    }
}