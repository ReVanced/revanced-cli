package app.revanced.patch

import app.revanced.patcher.data.base.Data
import app.revanced.patcher.patch.base.Patch
import java.io.File
import java.net.URLClassLoader

internal object Patches {

    /**
     * This method loads patches from a given patch file
     * @return the loaded patches represented as a list of functions returning instances of [Patch]
     */
    internal fun load(patchesJar: File): List<() -> Patch<Data>> {
        val url = patchesJar.toURI().toURL()
        val classLoader = URLClassLoader(arrayOf(url))

        val indexClass = classLoader.loadClass("app.revanced.patches.Index")

        val index = indexClass.declaredFields.last()
        index.isAccessible = true

        @Suppress("UNCHECKED_CAST")
        return index.get(null) as List<() -> Patch<Data>>
    }
}
