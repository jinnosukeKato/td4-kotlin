package io.github.jinnosukeKato.assembler

sealed interface Token {
    data class Mov(val register: Register, val immediate: Int) : Token

    data object MovAB : Token

    data object MovBA : Token

    data class Add(val register: Register, val immediate: Int) : Token

    data class Jmp(val immediate: Int) : Token

    data class Jnc(val immediate: Int) : Token

    data class In(val register: Register) : Token

    data class OutIm(val immediate: Int) : Token

    data object OutB : Token
}
