package io.github.jinnosukeKato.cpu

import io.github.jinnosukeKato.cpu.OpCode.*

class TD4(binPath: String, input: Int = 0b0000) {
    private val memory = Memory(binPath)
    private val register = Register()
    val port = Port(input and 0b1111)

    fun fetch(): Int {
        val pc = register.pc
        return memory.memory[pc]
    }

    fun decode(data: Int): Pair<OpCode, Int> {
        val opcode =
            OpCode.entries
                .firstOrNull { it.binCode == (data shr 4) } // 8bit中の上位4bit
                ?: throw RuntimeException("Illegal Opcode ${(data shr 4).toString(2)}")

        val operand = data and 0b1111 // 8bit中の下位4bit
        return Pair(opcode, operand)
    }

    fun execute(
        opcode: OpCode,
        operand: Int,
    ) {
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
        }

        register.pc = (register.pc + 1) and 0b1111
    }
}
