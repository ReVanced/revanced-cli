package app.revanced.cli.utils.patch

import java.io.File
import java.net.URL
import java.net.URLClassLoader

class PatchLoader {
    companion object {
        fun injectPatches(file: File) {
            // This function will fail on Java 9 and above.
            try {
                val url = file.toURI().toURL()
                val classLoader = Thread.currentThread().contextClassLoader as URLClassLoader
                val method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
                method.isAccessible = true
                method.invoke(classLoader, url)
            } catch (e: Exception) {
                throw Exception(
                    "Failed to inject patches! The CLI does NOT work on Java 9 and above, please use Java 8!",
                    e // propagate exception
                )
            }
        }
    }
}