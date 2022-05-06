package app.revanced.cli

import picocli.CommandLine

internal fun main(args: Array<String>) {
    CommandLine(MainCommand).execute(*args)
}