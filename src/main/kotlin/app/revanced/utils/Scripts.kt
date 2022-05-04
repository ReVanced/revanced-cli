package app.revanced.utils

// TODO: make this a class with PACKAGE_NAME as argument, then use that everywhere.
// make sure to remove the "const" from all the vals, they won't compile obviously.
internal object Scripts {
    private const val PACKAGE_NAME = "com.google.android.apps.youtube.music"
    private const val DATA_PATH = "/data/adb/ReVanced"
    internal const val APK_PATH = "/sdcard/base.apk"
    internal const val SCRIPT_PATH = "/sdcard/mount.sh"

    internal val MOUNT_SCRIPT =
        """
            base_path="$DATA_PATH/base.apk"
            stock_path=${'$'}{ pm path $PACKAGE_NAME | grep base | sed 's/package://g' }
            umount -l ${'$'}stock_path
            rm ${'$'}base_path
            mv "$APK_PATH"  ${'$'}base_path
            chmod 644 ${'$'}base_path
            chown system:system ${'$'}base_path
            chcon u:object_r:apk_data_file:s0  ${'$'}base_path
            mount -o bind ${'$'}base_path ${'$'}stock_path
        """.trimIndent()

    internal const val PIDOF_APP_COMMAND = "pidof -s $PACKAGE_NAME"
    private const val PIDOF_APP = "\$($PIDOF_APP_COMMAND)"
    internal const val CREATE_DIR_COMMAND = "su -c \"mkdir -p $DATA_PATH/\""
    internal const val MV_MOUNT_COMMAND = "su -c \"mv /sdcard/mount.sh $DATA_PATH/\""
    internal const val CHMOD_MOUNT_COMMAND = "su -c \"chmod +x $DATA_PATH/mount.sh\""
    internal const val START_MOUNT_COMMAND = "su -c $DATA_PATH/mount.sh"
    internal const val UNMOUNT_COMMAND =
        "su -c \"umount -l $(pm path $PACKAGE_NAME | grep base | sed 's/package://g')\""
    internal const val LOGCAT_COMMAND = "su -c \"logcat -c && logcat --pid=$PIDOF_APP\""
    internal const val STOP_APP_COMMAND = "su -c \"kill $PIDOF_APP\""
    internal const val START_APP_COMMAND = "monkey -p $PACKAGE_NAME 1"
}