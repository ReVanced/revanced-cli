package app.revanced.cli.patcher

import app.revanced.patcher.PatchLogger

object PatcherLogger : PatchLogger {
    private const val prefix = "[patcher]"

    override fun error(msg: String) {
        println("error: $prefix: $msg")
    }

    override fun info(msg: String) {
        println("info: $prefix: $msg")
    }

    override fun trace(msg: String) {
        println("trace: $prefix: $msg")
    }

    override fun warn(msg: String) {
        println("warn: $prefix: $msg")
    }
}