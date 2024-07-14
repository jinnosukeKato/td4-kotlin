package io.github.jinnosukeKato.cpu

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
}
