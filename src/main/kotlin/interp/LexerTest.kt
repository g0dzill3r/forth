package interp

import interpreter

/**
 *
 */

fun main (args: Array<String>) {
    interpreter ("lexer> ") {
        val seq = ForthLexer.lexer (it).iterator ()
        while (seq.hasNext ()) {
            println (seq.next ())
        }
        true
    }
}

// EOF