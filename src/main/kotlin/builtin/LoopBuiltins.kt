package builtin

import Builtin
import ForthMachine
import PeekableIterator
import Token
import java.util.*

val LOOP_BULTINS = listOf (
    Do::class,
    Begin::class,
    Leave::class
)

val LOOP_EXTRAS = listOf<String> ()

interface Absorber {
    fun absorb (token: Token, stack: Stack<Absorber>)
}

/**
 * Implements the definite looping construct which has the following forms:
 *
 * <end> <start> DO <ops> LOOP
 * <end> <start> DO <ops> <delta> +LOOP
 */

class Do: Builtin(NAME) {
    companion object {
        const val NAME= "DO"
        const val LOOP = "LOOP"
        const val LOOP_PLUS = "+LOOP"
    }

    private class DoAbsorber : Absorber {
        override fun absorb (token: Token, stack: Stack<Absorber>) {
            if (token is Token.Word) {
                when (token.word) {
                    NAME -> stack.push (DoAbsorber ())
                    LOOP, LOOP_PLUS -> stack.pop ()
                }
            }
            return
        }
    }

    fun parseDo (iter: PeekableIterator<Token>) : Pair<List<Token>, Boolean> {
        val ops = mutableListOf<Token> ()
        val absorbers = Stack<Absorber> ()

        while (true) {
            if (! iter.hasNext ()) {
                error ("Unterminated DO expression")
            }
            val next = iter.next ()
            if (absorbers.isNotEmpty ()) {
                absorbers.peek ().absorb (next, absorbers)
                ops.add (next)
                continue
            }
            if (next is Token.Word) {
                if (next.word == NAME) {
                    absorbers.add (DoAbsorber ())
                    ops.add (next)
                } else if (next.word == LOOP || next.word == LOOP_PLUS) {
                    return Pair (ops, next.word == Do.LOOP_PLUS)
                } else {
                    ops.add (next)
                }
            } else {
                ops.add (next)
            }
        }

        // NOT REACHED
    }

    private fun update (index: Int, depth: Int, list: List<Token>): List<Token> {
        val word = Char ('I'.code + depth - 1).toString ()
        return list.map {
            if (it is Token.Word && it.word == word) {
                Token.Word ("$index", it.loc)
            } else {
                it
            }
        }
    }

    /**
     * We'll keep a list as a sort of primitive stack that we can used to keep track of
     * the per-declaration invocation count so we can map the variables (I, J, ...) properly.
     */

    private val invocation = mutableListOf<Int> (0)
    private val depth: Int
        get () = invocation.last ()

    fun push () = invocation.add (0)
    fun pop () = invocation.removeAt (invocation.size - 1)
    private fun inc () = invocation[invocation.size - 1] ++
    private fun dec () = invocation[invocation.size - 1] --

    /**
     *
     */

    override fun perform (iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        try {
            inc ()
            val start = sm.pop()
            val end = sm.pop()
            val (loop, isPlus) = parseDo (iter)

            var i = start
            do {
                val updated = update (i, depth, loop)
                sm.execute (updated, terminal)
                val delta = if (isPlus) {
                    sm.pop ()
                } else {
                    1
                }
                i += delta
                if (delta > 0) {
                    if (i >= end) {
                        break
                    }
                } else {
                    if (i < end) {
                        break
                    }
                }
            } while (true)
        }
        catch (leave: LeaveException) {
            // IGNORED
        }
        finally {
            dec ()
        }
        return
    }
}

/**
 * Implements the indefinite looping construct which takes the following forms:
 *
 * BEGIN <ops> <flag> UNTIL
 * forth> 1 BEGIN DUP . 1 + DUP 10 > UNTIL
 * 1 2 3 4 5 6 7 8 9 10  ok
 *
 * BEGIN <ops> AGAIN
 * forth> BEGIN ."Hello" CR AGAIN
 *
 *  BEGIN <ops> WHILE <more> REPEAT
 *  forth>
 */


class Begin : Builtin (NAME) {
    companion object {
        const val NAME = "BEGIN"
        const val UNTIL = "UNTIL"
        const val REPEAT = "REPEAT"
        const val AGAIN = "AGAIN"
        const val WHILE = "WHILE"
    }

    private class BeginAbsorber : Absorber {
        private var sawWhile = false

        override fun absorb (token: Token, stack: Stack<Absorber>) {
            if (token is Token.Word) {
                when (token.word) {
                    NAME -> stack.push (BeginAbsorber ())
                    AGAIN, REPEAT, UNTIL -> stack.pop ()
                    WHILE -> {
                        if (sawWhile) {
                            error ("Unexpected WHILE encountered.")
                        } else {
                            sawWhile = true
                        }
                    }
                }
            }
            return
        }
    }

    fun parseBegin (iter: Iterator<Token>): Triple<List<Token>, Token.Word, Int?> {
        val ops = mutableListOf<Token> ()
        val absorbers = Stack<Absorber> ()
        var whileIndex: Int? = null

        while (true) {
            if (! iter.hasNext ()) {
                kotlin.error("Unterminated BEGIN expression.")
            }
            val next = iter.next ()
            if (absorbers.isNotEmpty ()) {
                absorbers.peek ().absorb (next, absorbers)
                ops.add (next)
                continue
            }
            if (next is Token.Word) {
                when (next.word) {
                    NAME -> {
                        absorbers.push (BeginAbsorber ())
                        ops.add (next)
                    }
                    REPEAT, UNTIL, AGAIN -> {
                        if (next.word == REPEAT && whileIndex == null) {
                            error ("Missing WHILE in BEGIN .. REPEAT expression.")
                        }
                        return Triple (ops, next, whileIndex)
                    }
                    WHILE -> {
                        if (whileIndex != null) {
                            kotlin.error("Unexpected WHILE encountered.")
                        } else {
                            whileIndex = ops.size
                            ops.add (next)
                        }
                    }
                    else -> ops.add (next)
                }
            } else {
                ops.add (next)
            }
        }
        // NOT REACHED
    }

    /**
     * Performs the list of operations indefinitely. Can only be halted by an exception
     * or a LEAVE operation.
     *
     * BEGIN ... AGAIN
     *
     * forth> 1 BEGIN DUP . 1 + DUP 9 > IF LEAVE THEN AGAIN
     * 1 2 3 4 5 6 7 8 9  ok
     */

    private fun performAgain (ops: List<Token>, sm: ForthMachine, terminal: StringBuffer) {
        try {
            while (true) {
                sm.execute (ops, terminal)
            }
        }
        catch (leave: LeaveException) {
            // IGNORED
        }
    }

    /**
     * Performs the list of operations
     *
     * BEGIN xx f WHILE yyy REPEAT
     *
     * 1 BEGIN DUP . 1 + DUP 10 < WHILE DUP . REPEAT
     * 1 2 2 3 3 4 4 5 5 6 6 7 7 8 8 9 9  ok
     */

    private fun performRepeat (first: List<Token>, second: List<Token>, sm: ForthMachine, terminal: StringBuffer) {
        try {
            while (true) {
                sm.execute (first, terminal)
                if (! sm.popBoolean()) {
                    break
                }
                sm.execute (second, terminal)
            }
        }
        catch (leave: LeaveException) {
            // IGNORED
        }
        return
    }

    /**
     * Peform until the sequence of operations leaves a true value on the stack.
     *
     * BEGIN xxx f UNTIL
     *
     * forth>  1 BEGIN DUP . 1 + DUP 9 > UNTIL
     * 1 2 3 4 5 6 7 8 9  ok
     */

    private fun performUntil (ops: List<Token>, sm: ForthMachine, terminal: StringBuffer) {
        try {
            while (true) {
                sm.execute (ops, terminal)
                if (sm.popBoolean ()) {
                    break
                }
            }
        }
        catch (leave: LeaveException) {
            // IGNORED
        }
        return
    }

    override fun perform (iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (ops, type, index) = parseBegin (iter)

        when (type.word) {
            AGAIN -> performAgain (ops, sm, terminal)
            REPEAT -> {
                index as Int
                val first = ops.subList (0, index)
                val second = ops.subList (index + 1, ops.size)
                performRepeat (first, second, sm, terminal)
            }
            UNTIL -> performUntil (ops, sm, terminal)
            else -> error ("Unrecognized BEGIN terminus: ${type.word}")
        }
        return
    }
}

class LeaveException : Exception ("LEAVE exception outside of enclosing BEGIN")

class Leave: Builtin(NAME) {
    companion object {
        const val NAME = "LEAVE"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        throw LeaveException ()
    }
}

// EOF