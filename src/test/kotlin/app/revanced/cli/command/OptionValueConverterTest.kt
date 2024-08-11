package app.revanced.cli.command

import kotlin.test.Test

class OptionValueConverterTest {
    @Test
    fun `converts to string`() {
        "string" convertsTo "string" because "Strings should remain the same"
    }

    @Test
    fun `converts to null`() {
        "null" convertsTo null because "null should convert to null"
        "\"null\"" convertsTo "null" because "Escaped null should convert to a string"
    }

    @Test
    fun `converts to boolean`() {
        "true" convertsTo true because "true should convert to a boolean true"
        "True" convertsTo true because "Casing should not matter"
        "\"true\"" convertsTo "true" because "Escaped booleans should be converted to strings"
        "\'True\'" convertsTo "True" because "Casing in escaped booleans should not matter"
        "tr ue" convertsTo "tr ue" because "Malformed booleans should be converted to strings"
    }

    @Test
    fun `converts to numbers`() {
        "1" convertsTo 1 because "Integers should convert to integers"
        "1.0" convertsTo 1.0 because "Doubles should convert to doubles"
        "1.0f" convertsTo 1.0f because "The suffix f should convert to a float"
        Long.MAX_VALUE.toString() convertsTo Long.MAX_VALUE because "Values that are too large for an integer should convert to longs"
        "1L" convertsTo 1L because "The suffix L should convert to a long"
    }

    @Test
    fun `converts escaped numbers to string`() {
        "\"1\"" convertsTo "1" because "Escaped numbers should convert to strings"
        "\"1.0\"" convertsTo "1.0" because "Escaped doubles should convert to strings"
        "\"1L\"" convertsTo "1L" because "Escaped longs should convert to strings"
        "\'1\'" convertsTo "1" because "Single quotes should not be treated as escape symbols"
        "\'.0\'" convertsTo ".0" because "Single quotes should not be treated as escape symbols"
        "\'1L\'" convertsTo "1L" because "Single quotes should not be treated as escape symbols"
    }

    @Test
    fun `trims escape symbols once`() {
        "\"\"\"1\"\"\"" convertsTo "\"\"1\"\"" because "The escape symbols should be trimmed once"
        "\'\'\'1\'\'\'" convertsTo "''1''" because "Single quotes should not be treated as escape symbols"
    }

    @Test
    fun `converts lists`() {
        "1,2" convertsTo "1,2" because "Lists without square brackets should not be converted to lists"
        "[1,2" convertsTo "[1,2" because "Invalid lists should not be converted to lists"
        "\"[1,2]\"" convertsTo "[1,2]" because "Lists with escaped square brackets should not be converted to lists"

        "[]" convertsTo emptyList<Any>() because "Empty untyped lists should convert to empty lists of any"
        "int[]" convertsTo emptyList<Int>() because "Empty typed lists should convert to lists of the specified type"
        "[[int[]]]" convertsTo listOf(listOf(emptyList<Int>())) because "Nested typed lists should convert to nested lists of the specified type"
        "[\"int[]\"]" convertsTo listOf("int[]") because "Lists of escaped empty typed lists should not be converted to lists"

        "[1,2,3]" convertsTo listOf(1, 2, 3) because "Lists of integers should convert to lists of integers"
        "[[1]]" convertsTo listOf(listOf(1)) because "Nested lists with one element should convert to nested lists"
        "[[1,2],[3,4]]" convertsTo listOf(listOf(1, 2), listOf(3, 4)) because "Nested lists should convert to nested lists"

        "[\"1,2\"]" convertsTo listOf("1,2") because "Values in lists should not be split by commas in strings"
        "[[\"1,2\"]]" convertsTo listOf(listOf("1,2")) because "Values in nested lists should not be split by commas in strings"

        "[\"\\\"\"]" convertsTo listOf("\"") because "Escaped quotes in strings should be converted to quotes"
        "[[\"\\\"\"]]" convertsTo listOf(listOf("\"")) because "Escaped quotes in strings nested in lists should be converted to quotes"
        "[.1,.2f,,true,FALSE]" convertsTo listOf(.1, .2f, "", true, false) because "Values in lists should be converted to the correct type"
    }

    private val convert = OptionValueConverter()::convert

    private infix fun String.convertsTo(to: Any?) = convert(this) to to
    private infix fun Pair<Any?, Any?>.because(reason: String) = assert(this.first == this.second) { reason }
}
