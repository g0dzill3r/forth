import builtin.*
import java.util.*

interface Invokable {
    fun perform (iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer)
}

abstract class Builtin (val name: String): Invokable {
    fun error (message: String): Nothing  = throw IllegalStateException (message)
    override fun toString(): String = ": $name   <builtin> ;"
}

/**
 * https://www.forth.com/starting-forth/3-forth-editor-blocks-buffer/
 */

class ForthMachine {
    val returnStack = Stack<Int> ()
    val stack = Stack<Int> ()
    val isEmpty: Boolean
        get () = stack.isEmpty()
    val isNotEmpty: Boolean
        get () = stack.isNotEmpty()
    fun push (vararg els: Int) {
        els.forEach {
            stack.push (it)
        }
    }
    fun pop (): Int = stack.pop ()
    fun pop (count:Int) : List<Int> = buildList {
        repeat (count) {
            add (pop ())
        }
    }
    fun peek (): Int = stack.peek ()

    val dictionary = ForthDictionary ()

    val TRUE = -1
    val FALSE = 0
    fun isTrue (i: Int) = i != FALSE
    fun isFalse (i: Int) = i == FALSE
    fun popBoolean () = isTrue (pop ())
    fun peekBoolean () = isTrue (peek ())
    fun pushBoolean (b: Boolean) = push (if (b) TRUE else FALSE)

    fun execute (string: String): List<String> {
        val els = ForthParser.parse (string).iterator()
        return buildList {
            els.forEach {
                add (execute (it))
            }
        }
    }

    fun execute (list: List<Token>, terminal: StringBuffer) {
        try {
            val iter = list.iterator ().peekable()
            while (iter.hasNext ()) {
                when (val next = iter.next ()) {
                    is Token.IntValue -> stack.push (next.value)
                    is Token.QuotedString -> terminal.append (next.string)
                    is Token.Comment -> Unit
                    is Token.Word -> {
                        val word = next.word
                        val op = dictionary.get (word)
                        if (op != null) {
                            op.perform (iter, this, terminal)
                        } else {
                            terminal.append ("$word?")
                            return
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

    fun execute (stmt: Statement) : String {
        val terminal = StringBuffer ()
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
        listOf (BUILTINS, COND_BUILTINS, MATH_BUILTINS, RETSTACK_BUILTINS, LOOP_BULTINS).forEach {
            instances (it).forEach {
                dictionary.add (it)
            }
        }
        listOf (EXTRAS, COND_EXTRAS, BUILTIN_EXTRAS, MATH_EXTRAS, RETSTACK_EXTRAS, LOOP_EXTRAS).forEach {
            it.forEach {
                execute (it)
            }
        }
    }

    fun dump () {
        stack.forEachIndexed { index, i ->
            println("$index: $i")
        }
        println ("EOS")
        return
    }
}

// EOF