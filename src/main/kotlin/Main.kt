package io.github.jinnosukeKato

import io.github.jinnosukeKato.assembler.Compiler
import io.github.jinnosukeKato.assembler.Parser
import io.github.jinnosukeKato.cpu.TD4

fun main() {
    val parser = Parser("./src/main/resources/test3.tdasm")
    val tokenList = parser.parse()
    val compiler = Compiler(tokenList)
    val bin = compiler.compile()
    bin.forEach {
        println(it.toString(2).padStart(8, '0'))
    }

    val cpu = TD4("./src/main/resources/test2.bin", 0b1010)
    for (i in 0..100) {
        val data = cpu.fetch()
        val operation = cpu.decode(data)
        cpu.execute(operation.first, operation.second)
        println("$i ${cpu.port.output.toString(2)}")
    }
}
