package app.revanced.utils

import app.revanced.cli.command.PatchList
import app.revanced.cli.logging.CliLogger
import app.revanced.patcher.extensions.PatchExtensions.options
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.patch.NoSuchOptionException
import app.revanced.utils.Options.PatchOption.Option
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File


internal object Options {
    private var mapper = jacksonObjectMapper()

    /**
     * Serializes the options for the patches in the list.
     *
     * @param patches The list of patches to serialize.
     * @param prettyPrint Whether to pretty print the JSON.
     * @return The JSON string containing the options.
     * @see PatchList
     */
    fun serialize(patches: PatchList, prettyPrint: Boolean = false): String = patches
        .filter { it.options?.any() == true }
        .map { patch ->
            PatchOption(
                patch.patchName,
                patch.options!!.map { option -> Option(option.key, option.value) }
            )
        }
        // See https://github.com/revanced/revanced-patches/pull/2434/commits/60e550550b7641705e81aa72acfc4faaebb225e7.
        .distinctBy { it.patchName }
        .let {
            if (prettyPrint)
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(it)
            else
                mapper.writeValueAsString(it)
        }

    /**
     * Deserializes the options for the patches in the list.
     *
     * @param json The JSON string containing the options.
     * @return The list of [PatchOption]s.
     * @see PatchOption
     * @see PatchList
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun deserialize(json: String): Array<PatchOption> = mapper.readValue(json, Array<PatchOption>::class.java)

    /**
     * Sets the options for the patches in the list.
     *
     * @param json The JSON string containing the options.
     * @param logger The logger to use for logging.
     */
    fun PatchList.setOptions(json: String, logger: CliLogger? = null) {
        filter { it.options?.any() == true }.let { patches ->
            if (patches.isEmpty()) return

            val patchOptions = deserialize(json)

            patches.forEach { patch ->
                patchOptions.find { option -> option.patchName == patch.patchName }?.let {
                    it.options.forEach { option ->
                        try {
                            patch.options?.set(option.key, option.value)
                                ?: logger?.warn("${patch.patchName} has no options")
                        } catch (e: NoSuchOptionException) {
                            logger?.error(e.message ?: "Unknown error")
                        }
                    }
                }
            }
        }
    }

    /**
     * Sets the options for the patches in the list.
     *
     * @param file The file containing the JSON string containing the options.
     * @param logger The logger to use for logging.
     * @see setOptions
     */
    fun PatchList.setOptions(file: File, logger: CliLogger? = null) = setOptions(file.readText(), logger)

    /**
     * Data class for a patch and its [Option]s.
     *
     * @property patchName The name of the patch.
     * @property options The [Option]s for the patch.
     */
    internal data class PatchOption(
        val patchName: String,
        val options: List<Option>
    ) {

        /**
         * Data class for patch option.
         *
         * @property key The name of the option.
         * @property value The value of the option.
         */
        internal data class Option(val key: String, val value: Any?)
    }
}