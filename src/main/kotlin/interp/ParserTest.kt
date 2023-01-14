package interp

import ForthParser
import interpreter

fun main() {
    interpreter("parse> ") {
        val op = ForthParser.parse (it)
        op.forEach {
            println (it)
        }
        true
    }
}

// EOF