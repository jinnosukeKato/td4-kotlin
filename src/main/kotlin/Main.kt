package io.github.jinnosukeKato

fun main() {
    val cpu = TD4("./src/main/resources/test2.bin", 0b1010)
    for (i in 0..100) {
        val data = cpu.fetch()
        val operation = cpu.decode(data)
        cpu.execute(operation.first, operation.second)
        println("$i ${cpu.port.output.toString(2)}")
    }
}
