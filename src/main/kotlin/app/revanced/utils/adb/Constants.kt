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

    // initial directory to push files to via adb push
    internal const val PATH_INIT_PUSH = "/data/local/tmp/revanced.delete"

    // revanced module path
    internal const val PATH_REVANCED = "/data/adb/modules/revanced"

    // revanced apk path
    private const val PATH_REVANCED_APP = "$PATH_REVANCED/$PLACEHOLDER.apk"

    // (un)mount script paths
    internal const val PATH_MOUNT = "$PATH_REVANCED/service.sh"
    internal const val PATH_UMOUNT = "$PATH_REVANCED/post-fs-data.sh"
    private const val PATH_PROP = "$PATH_REVANCED/module.prop"

    // move to revanced apk path & set permissions
    internal const val COMMAND_PREPARE_MOUNT_APK =
        "base_path=\"$PATH_REVANCED_APP\" && mv $PATH_INIT_PUSH ${'$'}base_path && chmod 644 ${'$'}base_path && chown system:system ${'$'}base_path && chcon u:object_r:apk_data_file:s0  ${'$'}base_path"

    // install mount script & set permissions
    internal const val COMMAND_INSTALL_MOUNT = "mv $PATH_INIT_PUSH $PATH_MOUNT && $COMMAND_CHMOD_MOUNT $PATH_MOUNT"

    // install umount script & set permissions
    internal const val COMMAND_INSTALL_UMOUNT = "mv $PATH_INIT_PUSH $PATH_UMOUNT && $COMMAND_CHMOD_MOUNT $PATH_UMOUNT"

    // install magisk-module prop
    internal const val COMMAND_INSTALL_PROP = "mv $PATH_INIT_PUSH $PATH_PROP"

    // remove old revanced files
    private const val PATH_OLD_REVANCED_DIR = "/data/adb/revanced/"
    private const val PATH_OLD_MOUNT = "/data/adb/service.d/mount_revanced_$PLACEHOLDER.sh"
    private const val PATH_OLD_UNMOUNT = "/data/adb/post-fs-data.d/unmount_revanced_$PLACEHOLDER.sh"
    internal const val COMMAND_REMOVE_OLD_FILES = "rm -rf $PATH_OLD_REVANCED_DIR $PATH_OLD_MOUNT $PATH_OLD_UNMOUNT"

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

            chcon u:object_r:apk_data_file:s0  ${'$'}base_path
            mount -o bind ${'$'}base_path ${'$'}stock_path
        """.trimIndent()

    // magisk-module prop
    internal val CONTENT_MODULE_PROP =
        """
            id=revanced
            name=ReVanced Magisk Module
            version=1
            versionCode=1
            author=ReVanced
            description=This is ReVanced Magisk-Module. You can uninstall ReVanced by deleting this module.
        """.trimIndent()
}
