package interp

import ForthMachine
import ForthParser
import builtin.UserDefined
import interpreter

fun main() {
    val sm = ForthMachine()

    interpreter("forth> ") {
        var done = false
        when {
            it == "quit" -> done = true
            it == "ops" -> {
                sm.dictionary.list.forEach { (op, func) ->
                    println(func)
                }
            }
            else -> {
                val list = ForthParser.parse (it)
                list.forEach {
                    val res = sm.execute (it)
                    println("$res ok")
                }
            }
        }
        !done
    }
}

// EOF