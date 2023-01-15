package test

import PeekableIterator
import Token
import builtin.Do
import interpreter
import peekable

fun main() {
    interpreter ("do> ") {
        val iter = ForthLexer.lexer (it).iterator ().peekable()
        while (iter.hasNext ()) {
            val next = iter.next ()
            if (next is Token.Word && next.word == "DO") {
                val (ops, isPlus) = Do ().parseDo (iter)
                println ("isPlus: $isPlus")
                println ("ops: ${ops.map { it.render ()}}")
            } else {
                println (next.render ())
            }
        }
        true
    }
}

// EOF

