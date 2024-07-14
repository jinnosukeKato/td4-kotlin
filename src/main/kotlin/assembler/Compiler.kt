package io.github.jinnosukeKato.assembler

class Compiler(private val tokens: List<Token>) {
    fun compile(): List<UByte> {
        if (tokens.isEmpty()) {
            throw RuntimeException("Token list is empty.")
        }

        return buildList {
            for (token in tokens) {
                add(
                    when (token) {
                        is Token.Add ->
                            when (token.register) {
                                Register.A -> getBinaryCode(0b0000, token.immediate)
                                Register.B -> getBinaryCode(0b0101, token.immediate)
                            }
                        is Token.In ->
                            when (token.register) {
                                Register.A -> getBinaryCode(0b0010)
                                Register.B -> getBinaryCode(0b0110)
                            }
                        is Token.Jmp -> getBinaryCode(0b1111, token.immediate)
                        is Token.Jnc -> getBinaryCode(0b1110, token.immediate)
                        is Token.Mov ->
                            when (token.register) {
                                Register.A -> getBinaryCode(0b0011, token.immediate)
                                Register.B -> getBinaryCode(0b0111, token.immediate)
                            }
                        Token.MovAB -> getBinaryCode(0b0001)
                        Token.MovBA -> getBinaryCode(0b0100)
                        Token.OutB -> getBinaryCode(0b1001)
                        is Token.OutIm -> getBinaryCode(0b1011)
                    },
                )
            }
        }
    }

    private fun getBinaryCode(
        binOpCode: Int,
        immediate: Int = 0b0000,
    ): UByte {
        return ((binOpCode shl 4) or (immediate and 0b1111)).toUByte()
    }
}
