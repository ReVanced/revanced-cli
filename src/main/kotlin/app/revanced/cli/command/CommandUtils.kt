package app.revanced.cli.command

import picocli.CommandLine

class OptionKeyConverter : CommandLine.ITypeConverter<String> {
    override fun convert(value: String): String = value
}

class OptionValueConverter : CommandLine.ITypeConverter<Any?> {
    override fun convert(value: String?): Any? {
        value ?: return null

        return when {
            value.startsWith("[") && value.endsWith("]") -> {
                val innerValue = value.substring(1, value.length - 1)

                buildList {
                    var nestLevel = 0
                    var insideQuote = false
                    var escaped = false

                    val item = buildString {
                        for (char in innerValue) {
                            when (char) {
                                '\\' -> {
                                    if (escaped || nestLevel != 0) {
                                        append(char)
                                    }

                                    escaped = !escaped
                                }

                                '"', '\'' -> {
                                    if (!escaped) {
                                        insideQuote = !insideQuote
                                    } else {
                                        escaped = false
                                    }

                                    append(char)
                                }

                                '[' -> {
                                    if (!insideQuote) {
                                        nestLevel++
                                    }

                                    append(char)
                                }

                                ']' -> {
                                    if (!insideQuote) {
                                        nestLevel--

                                        if (nestLevel == -1) {
                                            return value
                                        }
                                    }

                                    append(char)
                                }

                                ',' -> if (nestLevel == 0) {
                                    if (insideQuote) {
                                        append(char)
                                    } else {
                                        add(convert(toString()))
                                        setLength(0)
                                    }
                                } else {
                                    append(char)
                                }

                                else -> append(char)
                            }
                        }
                    }

                    if (item.isNotEmpty()) {
                        add(convert(item))
                    }
                }
            }

            value.startsWith("\"") && value.endsWith("\"") -> value.substring(1, value.length - 1)
            value.startsWith("'") && value.endsWith("'") -> value.substring(1, value.length - 1)
            value.endsWith("f") -> value.dropLast(1).toFloat()
            value.endsWith("L") -> value.dropLast(1).toLong()
            value.equals("true", ignoreCase = true) -> true
            value.equals("false", ignoreCase = true) -> false
            value.toIntOrNull() != null -> value.toInt()
            value.toLongOrNull() != null -> value.toLong()
            value.toDoubleOrNull() != null -> value.toDouble()
            value.toFloatOrNull() != null -> value.toFloat()
            value == "null" -> null
            value == "int[]" -> emptyList<Int>()
            value == "long[]" -> emptyList<Long>()
            value == "double[]" -> emptyList<Double>()
            value == "float[]" -> emptyList<Float>()
            value == "boolean[]" -> emptyList<Boolean>()
            value == "string[]" -> emptyList<String>()
            else -> value
        }
    }
}
