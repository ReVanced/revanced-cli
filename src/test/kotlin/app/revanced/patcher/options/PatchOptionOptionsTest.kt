package app.revanced.patcher.options

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.data.Context
import app.revanced.patcher.patch.*
import app.revanced.utils.Options
import app.revanced.utils.Options.setOptions
import org.junit.jupiter.api.Test

class PatchOptionsTestPatch : BytecodePatch() {
    override fun execute(context: BytecodeContext): PatchResult {
        return PatchResultSuccess()
    }

    companion object : OptionsContainer() {
        var key1 by option(
            PatchOption.StringOption(
                "key1", null, "title1", "description1"
            )
        )

        var key2 by option(
            PatchOption.BooleanOption(
                "key2", true, "title2", "description2"
            )
        )
    }
}

internal object PatchOptionOptionsTest {
    private var patches = listOf(PatchOptionsTestPatch::class.java as Class<out Patch<Context>>)

    @Test
    fun serializeTest() {
        assert(SERIALIZED_JSON == Options.serialize(patches))
    }

    @Test
    fun loadOptionsTest() {
        patches.setOptions(CHANGED_JSON)

        assert(PatchOptionsTestPatch.key1 == "test")
        assert(PatchOptionsTestPatch.key2 == false)
    }

    private const val SERIALIZED_JSON =
        "[{\"patchName\":\"PatchOptionsTestPatch\",\"options\":[{\"key\":\"key1\",\"value\":null},{\"key\":\"key2\",\"value\":true}]}]"

    private const val CHANGED_JSON =
        "[{\"patchName\":\"PatchOptionsTestPatch\",\"options\":[{\"key\":\"key1\",\"value\":\"test\"},{\"key\":\"key2\",\"value\":false}]}]"

}