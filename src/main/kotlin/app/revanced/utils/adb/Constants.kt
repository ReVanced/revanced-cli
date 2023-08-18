package app.revanced.utils.adb

internal object Constants {
    internal const val PLACEHOLDER = "TEMPLATE_PACKAGE_NAME"

    internal const val PATH_INIT_PUSH = "/data/local/tmp/revanced.delete"
    internal const val PATH_INSTALLATION = "/data/adb/revanced/"
    internal const val PATH_PATCHED_APK = "$PATH_INSTALLATION$PLACEHOLDER.apk"
    internal const val PATH_MOUNT = "/data/adb/service.d/mount_revanced_$PLACEHOLDER.sh"

    internal const val COMMAND_DELETE = "rm -rf $PLACEHOLDER"
    internal const val COMMAND_CREATE_DIR = "mkdir -p"
    internal const val COMMAND_RESTART = "pm resolve-activity --brief $PLACEHOLDER | tail -n 1 | " +
            "xargs am start -n && kill ${'$'}(pidof -s $PLACEHOLDER)"

    internal const val COMMAND_PREPARE_MOUNT_APK = "base_path=\"$PATH_PATCHED_APK\" && " +
            "mv $PATH_INIT_PUSH ${'$'}base_path && " +
            "chmod 644 ${'$'}base_path && " +
            "chown system:system ${'$'}base_path && " +
            "chcon u:object_r:apk_data_file:s0  ${'$'}base_path"

    internal const val COMMAND_UMOUNT =
        "grep $PLACEHOLDER /proc/mounts | while read -r line; do echo ${'$'}line | cut -d \" \" -f 2 | sed 's/apk.*/apk/' | xargs -r umount -l; done"

    internal const val COMMAND_INSTALL_MOUNT = "mv $PATH_INIT_PUSH $PATH_MOUNT && chmod +x $PATH_MOUNT"

    internal const val CONTENT_MOUNT_SCRIPT =
        """
            #!/system/bin/sh
            MAGISKTMP="${'$'}(magisk --path)" || MAGISKTMP=/sbin
            MIRROR="${'$'}MAGISKTMP/.magisk/mirror"
            while [ "${'$'}(getprop sys.boot_completed | tr -d '\r')" != "1" ]; do sleep 1; done
            
            base_path="$PATH_PATCHED_APK"
            stock_path=${'$'}( pm path $PLACEHOLDER | grep base | sed 's/package://g' )

            chcon u:object_r:apk_data_file:s0  ${'$'}base_path
            mount -o bind ${'$'}MIRROR${'$'}base_path ${'$'}stock_path
        """
}
