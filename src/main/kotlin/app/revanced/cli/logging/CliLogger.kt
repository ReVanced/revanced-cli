package app.revanced.cli.logging

internal interface CliLogger {
     fun error(msg: String)
     fun info(msg: String)
     fun trace(msg: String)
     fun warn(msg: String)
}