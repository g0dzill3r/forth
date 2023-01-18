import builtin.*

interface Invokable {
    fun perform (iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer)
}

abstract class Builtin (val name: String): Invokable {
    fun error (message: String): Nothing  = throw IllegalStateException (message)
    override fun toString(): String = ": $name   <builtin> ;"
}

/**
 * A simple Forth interpreter.
 *
 * https://www.forth.com/starting-forth/3-forth-editor-blocks-buffer/
 * http://www.exemark.com/FORTH/eForthOverviewv5.pdf
 * https://galileo.phys.virginia.edu/classes/551.jvn.fall01/primer.htm#contents
 * https://thinking-forth.sourceforge.net/
 */

class ForthMachine {
    val dictionary = ForthDictionary ()
    val returnStack = ForthStack ()
    val variables = ForthVariables ()
    val stack = ForthStack ()
    var base: Int = 10

    /**
     *
     */

    fun execute (string: String): List<String> {
        val els = ForthParser.parse (string).iterator()
        val terminal = StringBuffer ()
        return buildList {
            els.forEach {
                add (execute (it, terminal))
            }
        }
    }

    /**
     * Attempt to parse an unrecognized word as a numeric amount using the prevailing radix.
     */

    private fun maybeNumeric (word: String): Int? {
        return try {
            Integer.parseInt (word, base)
        }
        catch (e: Exception) {
            null
        }
    }

    fun execute (list: List<Token>, terminal: StringBuffer) {
        try {
            val iter = list.iterator ().peekable()
            while (iter.hasNext ()) {
                when (val next = iter.next ()) {
                    is Token.QuotedString -> terminal.append (next.string)
                    is Token.Comment -> Unit
                    is Token.Word -> {
                        val word = next.word
                        val addr = variables.has (word)
                        if (addr != null) {
                            stack.push (addr)
                        } else {
                            val op = dictionary.get (word)
                            if (op != null) {
                                op.perform (iter, this, terminal)
                            } else {
                                val numeric = maybeNumeric (word)
                                if (numeric != null) {
                                    stack.push (numeric)
                                } else {
                                    terminal.append ("$word?")
                                    return
                                }
                            }
                        }
                    }
                    else -> throw IllegalStateException ("Unexpected token type: ${next::class.simpleName}")
                }
            }
        }
        catch (e: Exception) {
            stack.clear ()
            throw e
        }
        return
    }

    fun execute (stmt: Statement, terminal: StringBuffer = StringBuffer ()) : String {
        when (stmt) {
            is Statement.Expression -> execute (stmt, terminal)
            is Statement.Declaration -> execute (stmt)
        }
        return terminal.toString ()
    }

    private fun execute (expr: Statement.Expression, terminal: StringBuffer) = execute (expr.list, terminal)

    private fun execute (decl: Statement.Declaration) {
        val list = decl.list
        val name = list[0] as Token.Word
        val op = UserDefined (name.word, list.subList (1, list.size))
        dictionary.add (name.word, op)
        return
    }

    init {
        listOf (BUILTINS, COND_BUILTINS, MATH_BUILTINS, RETSTACK_BUILTINS, LOOP_BULTINS, UNSIGNED_BUILTINS, VARIABLES_BUILTINS).forEach {
            instances (it).forEach {
                dictionary.add (it)
            }
        }
        listOf (EXTRAS, COND_EXTRAS, BUILTIN_EXTRAS, MATH_EXTRAS, RETSTACK_EXTRAS, LOOP_EXTRAS, UNSIGNED_EXTRAS, VARIABLES_EXTRAS).forEach {
            it.forEach {
                execute (it)
            }
        }
    }
}

enum class Base (radix: Int) {
    HEX (16),
    OCTAL (8),
    DECIMAL (10)
}

// EOF