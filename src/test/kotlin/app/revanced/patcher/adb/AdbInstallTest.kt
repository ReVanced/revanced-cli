package app.revanced.patcher.adb

import app.revanced.cli.logging.CliLogger
import app.revanced.utils.adb.Adb
import org.junit.jupiter.api.Test

internal class AdbInstallTest {
    companion object {
        private const val DEVICE_SERIAL = "emulator-5554"

        val adb = Adb.UserAdb(DEVICE_SERIAL, object : CliLogger {
            override fun error(msg: String) = println(msg)
            override fun info(msg: String) = println(msg)
            override fun trace(msg: String) = println(msg)
            override fun warn(msg: String) = println(msg)
        })
    }

    /**
     * Test the installation of apk files.
     *
     * Note: This test requires an emulator with the arbitrary serial 'emulator-5554' to be running
     * and split apk files to be present in the root folder.
     */
    @Test
    fun `should install apk`() {
        val base = Adb.Apk("base.apk")
        val splits = arrayOf("split_config.en.apk", "split_config.x86.apk", "split_config.xhdpi.apk").map(Adb::Apk)

        adb.install(base, splits)
    }
}