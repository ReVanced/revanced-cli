package app.revanced.utils.adb

internal object Constants {
    internal const val PLACEHOLDER = "PLACEHOLDER"

    internal const val TMP_PATH = "/data/local/tmp/revanced.tmp"
    internal const val INSTALLATION_PATH = "/data/adb/revanced/"
    internal const val PATCHED_APK_PATH = "$INSTALLATION_PATH$PLACEHOLDER.apk"
    internal const val MOUNT_PATH = "/data/adb/service.d/mount_revanced_$PLACEHOLDER.sh"

    internal const val DELETE = "rm -rf $PLACEHOLDER"
    internal const val CREATE_DIR = "mkdir -p"
    internal const val RESTART = "pm resolve-activity --brief $PLACEHOLDER | tail -n 1 | " +
            "xargs am start -n && kill ${'$'}(pidof -s $PLACEHOLDER)"

    internal const val INSTALL_PATCHED_APK = "base_path=\"$PATCHED_APK_PATH\" && " +
            "mv $TMP_PATH ${'$'}base_path && " +
            "chmod 644 ${'$'}base_path && " +
            "chown system:system ${'$'}base_path && " +
            "chcon u:object_r:apk_data_file:s0  ${'$'}base_path"

    internal const val UMOUNT =
        "grep $PLACEHOLDER /proc/mounts | while read -r line; do echo ${'$'}line | cut -d \" \" -f 2 | sed 's/apk.*/apk/' | xargs -r umount -l; done"

    internal const val INSTALL_MOUNT = "mv $TMP_PATH $MOUNT_PATH && chmod +x $MOUNT_PATH"

    internal val MOUNT_SCRIPT =
        """
            #!/system/bin/sh
            MAGISKTMP="${'$'}(magisk --path)" || MAGISKTMP=/sbin
            MIRROR="${'$'}MAGISKTMP/.magisk/mirror"
            while [ "${'$'}(getprop sys.boot_completed | tr -d '\r')" != "1" ]; do sleep 1; done
            
            base_path="$PATCHED_APK_PATH"
            stock_path=${'$'}( pm path $PLACEHOLDER | grep base | sed 's/package://g' )

            chcon u:object_r:apk_data_file:s0  ${'$'}base_path
            mount -o bind ${'$'}MIRROR${'$'}base_path ${'$'}stock_path
        """.trimIndent()
}
