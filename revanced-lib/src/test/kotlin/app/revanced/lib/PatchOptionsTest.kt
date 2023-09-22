package app.revanced.lib

import app.revanced.lib.Options.setOptions
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.options.types.BooleanPatchOption.Companion.booleanPatchOption
import app.revanced.patcher.patch.options.types.StringPatchOption.Companion.stringPatchOption
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal object PatchOptionsTest {
    private var patches = setOf(PatchOptionsTestPatch)

    @Test
    @Order(1)
    fun serializeTest() {
        assert(SERIALIZED_JSON == Options.serialize(patches))
    }

    @Test
    @Order(2)
    fun loadOptionsTest() {
        patches.setOptions(CHANGED_JSON)

        assert(PatchOptionsTestPatch.option1 == "test")
        assert(PatchOptionsTestPatch.option2 == false)
    }

    private const val SERIALIZED_JSON =
        "[{\"patchName\":\"PatchOptionsTestPatch\",\"options\":[{\"key\":\"key1\",\"value\":null},{\"key\":\"key2\",\"value\":true}]}]"

    private const val CHANGED_JSON =
        "[{\"patchName\":\"PatchOptionsTestPatch\",\"options\":[{\"key\":\"key1\",\"value\":\"test\"},{\"key\":\"key2\",\"value\":false}]}]"

    object PatchOptionsTestPatch : BytecodePatch(name = "PatchOptionsTestPatch") {
        var option1 by stringPatchOption("key1", null, "title1", "description1")
        var option2 by booleanPatchOption("key2", true, "title2", "description2")

        override fun execute(context: BytecodeContext) {
            // Do nothing
        }
    }
}