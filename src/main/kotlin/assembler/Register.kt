package io.github.jinnosukeKato.assembler

enum class Register {
    A,
    B,
    ;

    companion object {
        fun getByString(str: String) =
            when (str) {
                "A" -> A
                "B" -> B
                else -> throw RuntimeException("Illegal register $str")
            }
    }
}
