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
        fun fromBinCode(code: UByte): OpCode {
            return entries
                .firstOrNull { it.binCode == code.toInt() }
                ?: throw RuntimeException("Illegal Opcode ${code.toString(2)}")
        }
    }
}

class TD4(binPath: String, input: UByte = 0u) {

    private class Memory(path: String) {

        var memory = mutableListOf<UByte>()

        init {
            val binFile = File(path)
            if (!binFile.isFile)
                throw RuntimeException("${Path(path).toAbsolutePath()} is not a file")

            File(path)
                .bufferedReader()
                .use { it.readLines() }
                .map { it.toUByte(2) } // radix = 2で2進数で解釈する
                .forEach { memory.add(it) }
        }
    }

    private class Register {
        var a: UByte = 0u
        var b: UByte = 0u
        var carry = false
        var pc: UByte = 0u
    }

    class Port(val input: UByte = 0u) {
        var output: UByte = 0u
    }

    private val memory = Memory(binPath)
    private val register = Register()
    val port = Port(input and (0b1111).toUByte())

    fun fetch(): Pair<OpCode, UByte>{
        val pc = register.pc
        val operation = memory.memory[pc.toInt()]

        val opcode = OpCode.fromBinCode((operation.toInt() shr 4).toUByte()) // 8bit中の上位4bit
        val operand = operation and (0b1111).toUByte() // 8bit中の下位4bit
        return Pair(opcode, operand)
    }

    fun execute(opcode: OpCode, operand: UByte) {
        when (opcode) {
            AddA -> {
                val result: UByte = (register.a + operand).toUByte()
                register.carry = ((0b1_0000).toUByte() and result) != 0.toUByte()
                register.a = (0b1111).toUByte() and result
            }
            AddB -> {
                val result = (register.b + operand).toUByte()
                register.carry = ((0b1_0000).toUByte() and result) != 0.toUByte()
                register.b = (0b1111).toUByte() and result
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

        register.pc = (register.pc + 1u).toUByte() and (0b1111).toUByte()
    }
}
