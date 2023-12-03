package app.revanced.cli.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompatiblePackage(
    val name: String,
    @SerialName("compatible_versions")
    val compatibleVersions: Set<String> = setOf()
) {
    constructor(from: app.revanced.patcher.patch.Patch.CompatiblePackage) : this(
        from.name,
        from.versions.orEmpty()
    )
}

@Serializable
data class PatchOption(
    val title: String?,
    val description: String? = null,
    val key: String,
    val default: String? = null,
    val valid: Map<String, String?> = mapOf(),
) {
    constructor(from: app.revanced.patcher.patch.options.PatchOption<*>) : this(
        from.title,
        from.description,
        from.key,
        from.default?.toString(),
        from.values?.mapValues { it.value.toString() }.orEmpty()
    )
}

@Serializable
data class Patch(
    val index: Int,
    val description: String? = null,
    val options: List<PatchOption> = listOf(),
    @SerialName("compatible_packages")
    val compatiblePackages: List<CompatiblePackage> = listOf(),
) {
    constructor(from: IndexedValue<app.revanced.patcher.patch.Patch<*>>) : this(
        from.index,
        from.value.description,
        from.value.options.values.map(::PatchOption),
        from.value.compatiblePackages?.map(::CompatiblePackage).orEmpty(),
    )
}

