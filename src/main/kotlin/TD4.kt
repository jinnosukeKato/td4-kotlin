package io.github.jinnosukeKato

import io.github.jinnosukeKato.OpCode.*
import java.io.File
import kotlin.io.path.Path
import kotlin.system.exitProcess

enum class OpCode(val binCode: Int) {
    MovA(0b0011),
    MovB(0b0111),
    MovAB(0b0001),
    MovBA(0b0100),
    AddA(0b0000),
    AddB(0b0101),
    InA(0b0010),
    InB(0b0110),
    OutIm(0b1011),
    OutB(0b1001),
    Jmp(0b1111),
    Jnc(0b1110),
    Kill(0b1101); // 本来存在しない．エミュレータ終了用．

    companion object {
        fun fromBinCode(code: Int): OpCode {
            return entries
                .firstOrNull { it.binCode == code }
                ?: throw RuntimeException("Illegal Opcode ${code.toString(2)}")
        }
    }
}

class TD4(binPath: String, input: Int = 0b0000) {

    private class Memory(path: String) {

        var memory = mutableListOf<Int>()

        init {
            val binFile = File(path)
            if (!binFile.isFile)
                throw RuntimeException("${Path(path).toAbsolutePath()} is not a file")

            File(path)
                .bufferedReader()
                .use { it.readLines() }
                .map { it.toInt(2) } // radix = 2で2進数で解釈する
                .forEach { memory.add(it) }
        }
    }

    private class Register {
        var a = 0
        var b = 0
        var carry = false
        var pc = 0
    }

    class Port(val input: Int = 0b0000) {
        var output = 0
    }

    private val memory = Memory(binPath)
    private val register = Register()
    val port = Port(input and 0b1111)

    fun fetch(): Pair<OpCode, Int>{
        val pc = register.pc
        val operation = memory.memory[pc]

        val opcode = OpCode.fromBinCode(operation shr 4) // 8bit中の上位4bit
        val operand = operation and 0b1111 // 8bit中の下位4bit
        return Pair(opcode, operand)
    }

    fun execute(opcode: OpCode, operand: Int) {
        when (opcode) {
            AddA -> {
                val result = register.a + operand
                register.carry = (0b1_0000 and result) != 0
                register.a = 0b1111 and result
            }
            AddB -> {
                val result = register.b + operand
                register.carry = (0b1_0000 and result) != 0
                register.b = 0b1111 and result
            }
            MovA -> {
                register.a = operand
                register.carry = false
            }
            MovB -> {
                register.b = operand
                register.carry = false
            }
            MovAB -> {
                register.a = register.b
                register.carry = false
            }
            MovBA -> {
                register.b = register.a
                register.carry = false
            }
            Jmp -> {
                register.pc = operand
                register.carry = false
                return
            }
            Jnc -> {
                if (!register.carry) {
                    register.pc = operand
                    register.carry = false
                    return
                }
            }
            InA -> {
                register.a = port.input
                register.carry = false
            }
            InB -> {
                register.b = port.input
                register.carry = false
            }
            OutB -> {
                port.output = register.b
                register.carry = false
            }
            OutIm -> {
                port.output = operand
                register.carry = false
            }
            Kill -> {
                println("exit TD4 emulator")
                exitProcess(0)
            }
        }

        register.pc = (register.pc + 1) and 0b1111
    }
}
