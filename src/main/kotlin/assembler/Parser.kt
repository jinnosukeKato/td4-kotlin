package io.github.jinnosukeKato.assembler

import java.io.File

class Parser(sourcePath: String) {
    private val source: List<String> =
        buildList {
            File(sourcePath)
                .bufferedReader()
                .readLines()
                .forEach { line ->
                    line.split(' ')
                        .forEach {
                            add(it)
                        }
                }
        }

    fun parse(): List<Token> {
        var pos = 0

        return buildList {
            while (pos < source.size) {
                val op = source[pos]
                when {
                    op == "MOV" -> {
                        pos += 1
                        val left = source[pos]

                        pos += 1
                        val right = source[pos]

                        if (left == "A" && right == "B") {
                            add(Token.MovAB)
                        } else if (left == "B" && right == "A") {
                            add(Token.MovBA)
                        } else {
                            add(Token.Mov(Register.getByString(left), right.toInt(2)))
                        }
                    }
                    op == "ADD" -> {
                        pos += 1
                        val left = source[pos]

                        pos += 1
                        val right = source[pos]

                        add(Token.Add(Register.getByString(left), right.toInt(2)))
                    }
                    op == "JMP" -> {
                        pos += 1
                        add(Token.Jmp(source[pos].toInt(2)))
                    }
                    op == "JNC" -> {
                        pos += 1
                        add(Token.Jnc(source[pos].toInt(2)))
                    }
                    op == "IN" -> {
                        pos += 1
                        add(Token.In(Register.getByString(source[pos])))
                    }
                    op == "OUT" -> {
                        pos += 1
                        if (source[pos] == "B") {
                            add(Token.OutB)
                        } else {
                            add(Token.OutIm(source[pos].toInt(2)))
                        }
                    }
                    op.isBlank() -> break
                    else -> throw RuntimeException("Illegal Opcode $op")
                }

                pos += 1
            }
        }
    }
}
