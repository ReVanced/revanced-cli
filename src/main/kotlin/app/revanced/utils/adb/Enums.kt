package app.revanced.utils.adb

enum class RootType(val value: String) {
    MAGISKSU("Magisk"),
    SUPERSU("SuperSU"),
    NONE_OR_UNSUPPORTED("none or unsupported")
}