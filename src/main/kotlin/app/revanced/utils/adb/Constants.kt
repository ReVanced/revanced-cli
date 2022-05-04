package app.revanced.utils.adb

internal object Constants {
    internal const val PLACEHOLDER = "TEMPLATE_PACKAGE_NAME"

    internal const val NAME_MOUNT_SCRIPT = "mount.sh"

    internal const val PATH_DATA = "/data/adb/revanced/"
    internal const val PATH_INIT_PUSH = "/sdcard/revanced"

    internal const val COMMAND_PID_OF = "pidof -s "
    internal const val COMMAND_CREATE_DIR = "mkdir -p "
    internal const val COMMAND_MOVE_BASE = "mv $PATH_INIT_PUSH $PATH_DATA/base.apk"
    internal const val COMMAND_MOVE_MOUNT = "mv $PATH_INIT_PUSH $PATH_DATA/$NAME_MOUNT_SCRIPT"
    internal const val COMMAND_CHMOD_MOUNT = "chmod +x $PATH_DATA"
    internal const val COMMAND_MOUNT = "./$PATH_DATA/$NAME_MOUNT_SCRIPT"
    internal const val COMMAND_UNMOUNT = "umount -l $(pm path $PLACEHOLDER | grep base | sed 's/package://g')"
    internal const val COMMAND_LOGCAT = "logcat -c && logcat --pid=$($COMMAND_PID_OF $PLACEHOLDER)"
    internal const val COMMAND_RUN_APP = "monkey -p $PLACEHOLDER 1"
    internal const val COMMAND_KILL_APP = "kill \$($COMMAND_PID_OF $PLACEHOLDER)"

    internal val CONTENT_MOUNT_SCRIPT =
        """
            base_path="$PATH_DATA/base.apk"
            stock_path=${'$'}{ pm path $PLACEHOLDER | grep base | sed 's/package://g' }
            umount -l ${'$'}stock_path
            rm ${'$'}base_path
            mv "$PATH_INIT_PUSH"  ${'$'}base_path
            chmod 644 ${'$'}base_path
            chown system:system ${'$'}base_path
            chcon u:object_r:apk_data_file:s0  ${'$'}base_path
            mount -o bind ${'$'}base_path ${'$'}stock_path
        """.trimIndent()
}