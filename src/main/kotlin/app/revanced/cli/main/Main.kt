package app.revanced.cli.main

import app.revanced.cli.command.MainCommand
import picocli.CommandLine

internal fun main(args: Array<String>) {
    CommandLine(MainCommand).execute(*args)
}