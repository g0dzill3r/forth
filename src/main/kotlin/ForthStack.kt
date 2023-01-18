import java.util.*

/**
 * Encapsulates the stack object for reuse as both the primary and the return stack.
 */

class ForthStack {
    val impl = Stack<Int> ()

    val size: Int
        get () = impl.size

    val isEmpty: Boolean
        get () = impl.isEmpty()

    val isNotEmpty: Boolean
        get () = impl.isNotEmpty()

    fun clear () {
        impl.clear ()
    }

    fun push (vararg els: Int) {
        els.forEach {
            impl.push (it)
        }
    }

    fun pop (): Int = impl.pop ()

    fun pop (count:Int) : List<Int> = buildList {
        repeat (count) {
            add (pop ())
        }
    }

    fun peek (): Int = impl.peek ()

    fun popBoolean () = ForthBoolean.isTrue (pop ())
    fun peekBoolean () = ForthBoolean.isTrue (peek ())
    fun pushBoolean (b: Boolean) = push (if (b) ForthBoolean.TRUE else ForthBoolean.FALSE)

    fun dump (radix: Int): String {
        return if (impl.isNotEmpty ()) {
            val els = impl.joinToString(" ") { it.toString (radix) }
            "<${impl.size}> $els"
        } else {
            "<0> EMPTY"
        }
    }

    override fun toString(): String = dump (10)
}

// EOF