package test

import Token
import builtin.Absorber
import builtin.Begin
import builtin.Do
import builtin.If
import interpreter
import peekable
import java.util.*

/**
 * BEGIN ... AGAIN
 * BEGIN xx f WHILE yyy REPEAT
 * BEGIN xxx f UNTIL
 */





fun main() {
    println ("""
         * BEGIN ... AGAIN
         * BEGIN xx f WHILE yyy REPEAT
         * BEGIN xxx f UNTIL
    """.trimIndent())

    interpreter ("begin> ") {
        val iter = ForthLexer.lexer (it).iterator ().peekable()
        while (iter.hasNext ()) {
            val next = iter.next ()
            if (next is Token.Word && next.word == "BEGIN") {
                val (ops, type, index) = Begin().parseBegin (iter)
                println ("type: $type")
                println ("index: $index")
                println ("ops: ${ops.map { it.render ()}}")
            } else {
                println (next.render ())
            }
        }
        true
    }
}

// EOF