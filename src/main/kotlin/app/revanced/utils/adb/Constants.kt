package app.revanced.utils.adb

internal object Constants {
    // template placeholder to replace a string in commands
    internal const val PLACEHOLDER = "TEMPLATE_PACKAGE_NAME"

    // utility commands
    private const val COMMAND_CHMOD_MOUNT = "chmod +x"
    internal const val COMMAND_PID_OF = "pidof -s"
    internal const val COMMAND_CREATE_DIR = "mkdir -p"
    internal const val COMMAND_LOGCAT = "logcat -c && logcat | grep AndroidRuntime"
    internal const val COMMAND_RESTART = "monkey -p $PLACEHOLDER 1 && kill ${'$'}($COMMAND_PID_OF $PLACEHOLDER)"

    // default mount file name
    private const val NAME_MOUNT_SCRIPT = "mount_revanced_$PLACEHOLDER.sh"

    // initial directory to push files to via adb push
    internal const val PATH_INIT_PUSH = "/sdcard/revanced.delete"

    // revanced path
    internal const val PATH_REVANCED = "/data/adb/revanced/"

    // revanced apk path
    private const val PATH_REVANCED_APP = "$PATH_REVANCED$PLACEHOLDER.apk"

    // (un)mount script paths
    internal const val PATH_MOUNT = "/data/adb/service.d/$NAME_MOUNT_SCRIPT"
    internal const val PATH_UMOUNT = "/data/adb/post-fs-data.d/un$NAME_MOUNT_SCRIPT"

    // move to revanced apk path & set permissions
    internal const val COMMAND_INSTALL_APK =
        "base_path=\"$PATH_REVANCED_APP\" && mv $PATH_INIT_PUSH ${'$'}base_path && chmod 644 ${'$'}base_path && chown system:system ${'$'}base_path && chcon u:object_r:apk_data_file:s0  ${'$'}base_path"

    // install mount script & set permissions
    internal const val COMMAND_INSTALL_MOUNT = "mv $PATH_INIT_PUSH $PATH_MOUNT && $COMMAND_CHMOD_MOUNT $PATH_MOUNT"

    // install umount script & set permissions
    internal const val COMMAND_INSTALL_UMOUNT = "mv $PATH_INIT_PUSH $PATH_UMOUNT && $COMMAND_CHMOD_MOUNT $PATH_UMOUNT"

    // unmount script
    internal val CONTENT_UMOUNT_SCRIPT =
        """
            #!/system/bin/sh
            
            stock_path=${'$'}( pm path $PLACEHOLDER | grep base | sed 's/package://g' )
            umount -l ${'$'}stock_path
        """.trimIndent()

    // mount script
    internal val CONTENT_MOUNT_SCRIPT =
        """
            #!/system/bin/sh
            while [ "${'$'}(getprop sys.boot_completed | tr -d '\r')" != "1" ]; do sleep 1; done
            
            base_path="$PATH_REVANCED_APP"
            stock_path=${'$'}( pm path $PLACEHOLDER | grep base | sed 's/package://g' )
            mount -o bind ${'$'}base_path ${'$'}stock_path
        """.trimIndent()
}
