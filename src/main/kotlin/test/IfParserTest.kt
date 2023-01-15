package test

import Token
import builtin.If
import interpreter
import peekable

fun main() {
    interpreter ("if> ") {
        val iter = ForthLexer.lexer (it).iterator ().peekable()
        while (iter.hasNext ()) {
            val next = iter.next ()
            if (next is Token.Word && next.word == "IF") {
                val (happy, sad) = If().parseIf (iter)
                println ("HAPPY: ${happy.map {it.render ()}}")
                println ("SAD:   ${sad?.map {it.render ()}}")
            } else {
                println (next.render ())
            }
        }
        true
    }
}

// EOF