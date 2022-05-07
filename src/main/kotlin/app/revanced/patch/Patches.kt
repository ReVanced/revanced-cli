package app.revanced.patch

import app.revanced.patcher.data.base.Data
import app.revanced.patcher.patch.base.Patch
import app.revanced.patches.Index
import java.io.File
import java.net.URLClassLoader

internal object Patches {


    /**
     * This method loads patches from a given patch file
     * @return the loaded patches represented as a list of functions returning instances of [Patch]
     */
    internal fun load(patchFile: File): List<() -> Patch<Data>> {
        val url = patchFile.toURI().toURL()
        val classLoader = URLClassLoader(arrayOf(url))
        return loadIndex(classLoader).patches
    }
    private fun loadIndex(classLoader: ClassLoader) = classLoader
        .loadClass(Index::class.java.canonicalName)
        .fields
        .first()
        .get(null) as Index
}
