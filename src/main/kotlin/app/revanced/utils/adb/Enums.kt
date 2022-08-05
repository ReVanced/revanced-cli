package app.revanced.utils.adb

enum class ROOT(val value: String) {
    MAGISKSU("Magisk"),
    SUPERSU("SuperSU"),
    NONE_OR_UNSUPPORTED("none or unsupported")
}