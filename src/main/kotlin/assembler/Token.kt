package io.github.jinnosukeKato.assembler

enum class Register {
    A,
    B;

    companion object {
        fun getByString(str: String) = when (str) {
            "A" -> A
            "B" -> B
            else -> throw RuntimeException("Illegal register $str")
        }
    }
}

sealed interface Token {
    data class Mov(val register: Register, val immediate: Int): Token
    data object MovAB: Token
    data object MovBA: Token
    data class Add(val register: Register, val immediate: Int): Token
    data class Jmp(val immediate: Int): Token
    data class Jnc(val immediate: Int): Token
    data class In(val register: Register): Token
    data class OutIm(val immediate: Int): Token
    data object OutB: Token
}
