package interp

import ForthMachine

fun main() {
    fun usage () {
        println ("""
            p - print buffer
            d - delete last line
            c - clear buffer
            e - execute buffer (or blank line)
            ? - show these commands
        """.trimIndent())
    }

    fun isValid (str: String): Boolean {
        return try {
            ForthParser.parse (str)
            true
        }
        catch (e: Exception) {
            false
        }
    }

    val fm = ForthMachine ()
    loop {
        absorb {
            val buf = StringBuffer ()
            while (true) {
                val count = buf.count { it == '\n' }
                print ("$count: ")
                val s = readln()
                if (s == "") {
                    // IGNORED
                } else if (s == "e") {
                    break
                } else if (s == "?") {
                    usage ()
                } else if (s == "p") {
                    val str = buf.toString ()
                    if (str.isNotEmpty()) {
                        println(str.substring(0, str.length - 1))
                    }
                } else if (s == "d") {
                    val i = buf.lastIndexOf("\n", buf.length - 2) + 1
                    buf.setLength (i)
                } else if (s == "c") {
                    buf.setLength (0)
                } else {
                    buf.append (s)
                    buf.append ("\n")
                }
            }

            // Execute the expressions that they provided

            try {
                val res = fm.execute (buf.toString ())
                println ("$res ok")
            }
            catch (e: Exception) {
                println (e.toString ())
            }
        }
    }
}

fun loop (func: () -> Unit): Nothing {
    while (true) {
        func()
    }
}

fun absorb (func: () -> Unit) {
    try {
        func ()
    }
    catch (e: Exception) {
        e.printStackTrace()
    }
}

// EOF