package io.github.jinnosukeKato.cpu

import java.io.File
import kotlin.io.path.Path

class Memory(path: String) {
    var memory = mutableListOf<Int>()

    init {
        val binFile = File(path)
        if (!binFile.isFile) {
            throw RuntimeException("${Path(path).toAbsolutePath()} is not a file")
        }

        File(path)
            .bufferedReader()
            .use { it.readLines() }
            .map { it.toInt(2) } // radix = 2で2進数で解釈する
            .forEach { memory.add(it) }
    }
}
