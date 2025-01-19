import java.io.*
import java.util.*

fun main(args: Array<String>) {
    val stopWords = readFileOrEmpty("stopwords.txt")
    val userInput = if (args.isEmpty() || args[0] == "-index") {
        tryPrintToSystemOut("Enter text: ", out = ::print)
        tryReadFromSystemIn().orEmpty().ifBlank {
            tryPrintToSystemOut("No input provided. Exiting.")
            return
        }
    } else {
        readFileOrEmpty(args[0]).joinToString(separator = " ")
    }
    val wordCandidates = userInput.split(delimiters = arrayOf(" ", "."))
    val words = wordCandidates.filter {
        it.isNotBlank().and(it.all { char -> char.isLetter().or(char == '-') }).and(it !in stopWords)
    }
    val averageWordLength = words.map { it.length }.average()
    val formattedAverageWordLength = String.format(Locale.US, "%.2f", averageWordLength)
    val output =
        "Number of words: ${words.size}, " + "unique: ${words.distinct().size}; " + "average word length: $formattedAverageWordLength characters"
    tryPrintToSystemOut(output)
    if (args.contains("-index").and(args.any { it.contains("-dict") })) {
        val dictionary = readFileOrEmpty(args.filter { it.contains("-dict") }[0].split("=")[1])
        val unknown = words.filter { it !in dictionary }
        val indexHeader = if (unknown.isNotEmpty()) "Index (unknown: ${unknown.size}):" else "Index:"
        tryPrintToSystemOut(indexHeader)
        words.toSortedSet { o1, o2 -> o1.compareTo(o2, ignoreCase = true) }.forEach {
            tryPrintToSystemOut(if (it in dictionary) it else "$it*")
        }
    } else if (args.contains("-index")) {
        tryPrintToSystemOut("Index:")
        words.toSortedSet { o1, o2 -> o1.compareTo(o2, ignoreCase = true) }.forEach {
            tryPrintToSystemOut(it)
        }
    }
}

private fun readFileOrEmpty(userInputFile: String): List<String> =
    File(userInputFile).takeIf { it.exists().and(it.isFile).and(it.canRead()) }?.readLines().orEmpty().also {
        if (it.isEmpty()) {
            tryPrintToSystemOut("The file '$userInputFile' was not found and will be treated as empty.")
        }
    }

fun tryPrintToSystemOut(message: String, out: (message: Any?) -> Unit = ::println) = try {
    out(message)
} catch (e: IOException) {
    System.err.println("Error: Unable to write to the output stream. ${e.message}")
} catch (e: IllegalStateException) {
    System.err.println("Error: Output stream is in an invalid state. ${e.message}")
} catch (e: Exception) {
    System.err.println("An unexpected error occurred while writing to the output: ${e.message}")
}

fun tryReadFromSystemIn(): String? = try {
    readln()
} catch (e: IOException) {
    System.err.println("Error: Unable to read input from the console. ${e.message}")
    null
} catch (e: IllegalStateException) {
    System.err.println("Error: Input stream is in an invalid state. ${e.message}")
    null
} catch (e: Exception) {
    System.err.println("An unexpected error occurred while reading input: ${e.message}")
    null
}
