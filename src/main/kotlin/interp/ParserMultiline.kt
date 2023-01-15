package interp

fun main() {
    val input = """
        1 1 + . 
        : FOO 123 ; 
        FOO 2 + . 
        .S
    """
    ForthParser.parse (input).forEachIndexed { i, s ->
        println ("[$i] $s")
    }
    return
}