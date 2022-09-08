package app.revanced.utils

import app.revanced.cli.command.MainCommand.logger
import app.revanced.patcher.data.Data
import app.revanced.patcher.extensions.PatchExtensions.options
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.patch.Patch
import cc.ekblad.toml.encodeTo
import cc.ekblad.toml.model.TomlValue
import cc.ekblad.toml.serialization.from
import cc.ekblad.toml.tomlMapper
import java.io.File

private typealias PatchList = List<Class<out Patch<Data>>>
private typealias OptionsMap = Map<String, Map<String, Any>>

private const val NULL = "null"

object OptionsLoader {
    @JvmStatic
    private val mapper = tomlMapper {}

    @JvmStatic
    fun init(file: File, patches: PatchList) {
        if (!file.exists()) file.createNewFile()
        val path = file.toPath()
        val map = mapper.decodeWithDefaults(
            generateDefaults(patches),
            TomlValue.from(path)
        ).also { mapper.encodeTo(path, it) }
        readAndSet(map, patches)
    }

    private fun readAndSet(map: OptionsMap, patches: PatchList) {
        for ((patchName, options) in map) {
            val patch = patches.find { it.patchName == patchName } ?: continue
            val patchOptions = patch.options ?: continue
            for ((key, value) in options) {
                try {
                    logger.info("Setting option '${key}' to value: '${value}'")
                    patchOptions[key] = value.let {
                        if (it == NULL) null else it
                    }
                } catch (e: Exception) {
                    logger.warn("Error while setting option $key for patch $patchName: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }

    private fun generateDefaults(patches: PatchList) = buildMap {
        for (patch in patches) {
            val options = patch.options ?: continue
            if (!options.iterator().hasNext()) continue
            put(patch.patchName, buildMap {
                for (option in options) {
                    put(option.key, option.value ?: NULL)
                }
            })
        }
    }
}