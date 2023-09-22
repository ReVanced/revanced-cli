package app.revanced.lib

import app.revanced.patcher.PatchSet
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal object PatchUtilsTest {
    @Test
    fun `return 'a' because it is the most common version`() {
        val patches = arrayOf("a", "a", "c", "d", "a", "b", "c", "d", "a", "b", "c", "d")
            .map { version -> newPatch("some.package", version) }
            .toSet()

        assertEqualsVersion("a", patches, "some.package")
    }

    @Test
    fun `return null because no patches were supplied`() {
        assertEqualsVersion(null, emptySet<BytecodePatch>(), "some.package")
    }

    @Test
    fun `return null because no patch is compatible with the supplied package name`() {
        val patches = setOf(newPatch("some.package", "a"))

        assertEqualsVersion(null, patches, "other.package")
    }

    @Test
    fun `return null because no patch compatible package is constrained to a version`() {
        val patches = setOf(
            newPatch("other.package"),
            newPatch("other.package"),
        )

        assertEqualsVersion(null, patches, "other.package")
    }

    private fun assertEqualsVersion(
        expected: String?, patches: PatchSet, compatiblePackageName: String
    ) = assertEquals(expected, PatchUtils.getMostCommonCompatibleVersion(patches, compatiblePackageName))

    private fun newPatch(packageName: String, vararg versions: String) = object : BytecodePatch(
        compatiblePackages = setOf(CompatiblePackage(packageName, versions.toSet()))
    ) {
        override fun execute(context: BytecodeContext) {}
    }
}