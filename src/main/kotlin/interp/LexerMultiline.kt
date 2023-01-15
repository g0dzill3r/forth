package interp

fun main() {
    val input = """
        1 1 + . 
        1 2 + . 
        .S
    """
    ForthLexer.lexer(input).iterator().forEach {
        println (it)
    }
    return
}