package app.revanced.cli.utils

// TODO: make this a class with PACKAGE_NAME as argument, then use that everywhere.
// make sure to remove the "const" from all the vals, they won't compile obviously.
object Scripts {
    private const val PACKAGE_NAME = "com.google.android.youtube"
    private const val DATA_PATH = "/data/adb/ReVanced"
    const val APK_PATH = "/sdcard/base.apk"
    const val SCRIPT_PATH = "/sdcard/mount.sh"

    val MOUNT_SCRIPT =
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

    const val PIDOF_APP_COMMAND = "pidof -s $PACKAGE_NAME"
    private const val PIDOF_APP = "\$($PIDOF_APP_COMMAND)"
    const val CREATE_DIR_COMMAND = "su -c \"mkdir -p $DATA_PATH/\""
    const val MV_MOUNT_COMMAND = "su -c \"mv /sdcard/mount.sh $DATA_PATH/\""
    const val CHMOD_MOUNT_COMMAND = "su -c \"chmod +x $DATA_PATH/mount.sh\""
    const val START_MOUNT_COMMAND = "su -c $DATA_PATH/mount.sh"
    const val UNMOUNT_COMMAND = "su -c \"umount -l $(pm path $PACKAGE_NAME | grep base | sed 's/package://g')\""
    const val LOGCAT_COMMAND = "su -c \"logcat -c && logcat --pid=$PIDOF_APP\""
    const val STOP_APP_COMMAND = "su -c \"kill $PIDOF_APP\""
    const val START_APP_COMMAND = "monkey -p $PACKAGE_NAME 1"
}