package builtin

import Builtin
import ForthMachine
import PeekableIterator
import Token
import java.util.*

val COND_BUILTINS = listOf (
    Equals::class,
    NotEquals::class,
    LessThan::class,
    GreaterThan::class,
    ZeroEquals::class,
    ZeroLessThan::class,
    ZeroGreaterThan::class,
    And::class,
    Or::class,
    MaybeDup::class,
    Abort::class,
    If::class,
    Invert::class
)

val COND_EXTRAS = listOf (
    ": TRUE -1 ;",
    ": FALSE 0 ;"
)

class Equals : Builtin (NAME) {
    companion object {
        const val NAME = "="
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.pop (2)
        sm.pushBoolean (a == b)
    }
}

class NotEquals : Builtin (NAME) {
    companion object {
        const val NAME = "<>"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.pop (2)
        sm.pushBoolean (a != b)
    }
}

class LessThan : Builtin (NAME) {
    companion object {
        const val NAME = "<"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.pop (2)
        sm.pushBoolean (b < a)

    }
}

class GreaterThan : Builtin (NAME) {
    companion object {
        const val NAME = ">"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.pop (2)
        sm.pushBoolean (b > a)
    }
}

class ZeroEquals : Builtin (NAME) {
    companion object {
        const val NAME = "0="
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.pushBoolean (sm.pop () == 0)
    }
}

class ZeroLessThan : Builtin (NAME) {
    companion object {
        const val NAME = "0<"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.pushBoolean (sm.pop () < 0)
    }
}

class ZeroGreaterThan : Builtin (NAME) {
    companion object {
        const val NAME = "0>"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.pushBoolean (sm.pop () > 0)
    }
}

class And : Builtin (NAME) {
    companion object {
        const val NAME = "AND"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.pushBoolean (sm.popBoolean() && sm.popBoolean())
        return
    }
}
class Or : Builtin (NAME) {
    companion object {
        const val NAME = "OR"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.pushBoolean (sm.popBoolean() || sm.popBoolean())
        return
    }
}

class MaybeDup: Builtin (NAME) {
    companion object {
        const val NAME = "?DUP"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        if (sm.peekBoolean()) {
            sm.push (sm.peek ())
        }
        return
    }
}

class Abort: Builtin(NAME) {
    companion object {
        const val NAME = "ABORT"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        if (sm.popBoolean ()) {
            throw IllegalStateException ("<ERROR>")
        }
        return
    }
}
class Invert: Builtin (NAME) {
    companion object {
        const val NAME = "INVERT"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.pushBoolean (! sm.popBoolean ())
        return
    }
}

/**
 * Conditional logic statement.
 *
 * IF <ops> THEN
 *
 * IF <ops> ELSE <ops> THEN
 */

class If: Builtin(NAME) {
    companion object {
        const val NAME = "IF"
        const val THEN = "THEN"
        const val ELSE = "ELSE"
    }

    private class IfAbsorber : Absorber {
        private var sawElse = false

        override fun absorb (token: Token, stack: Stack<Absorber>) {
            if (token is Token.Word) {
                when (token.word) {
                    NAME -> stack.push (IfAbsorber ())
                    ELSE -> {
                        if (sawElse) {
                            error ("Unexpected ELSE encountered.")
                        } else {
                            sawElse = true
                        }
                    }
                    THEN -> stack.pop ()
                }
            }
            return
        }
    }

    fun parseIf (iter: PeekableIterator<Token>): Pair<List<Token>, List<Token>?> {
        val happy = mutableListOf<Token> ()
        var sad: MutableList<Token>? = null
        val absorbers = Stack<Absorber> ()

        fun addToken (token: Token) = (sad ?: happy).add (token)

        while (true) {
            if (! iter.hasNext ()) {
                error ("Unterminated IF expression.")
            }
            val next = iter.next ()
            if (absorbers.isNotEmpty ()) {
                absorbers.peek ().absorb (next, absorbers)
                addToken (next)
                continue
            }
            if (next is Token.Word) {
                when (next.word) {
                    NAME -> {
                        absorbers.push (IfAbsorber ())
                        addToken (next)
                    }
                    ELSE -> {
                        if (sad != null) {
                            error ("Unexpected ELSE encountered.")
                        }
                        sad = mutableListOf ()
                    }
                    THEN ->break
                    else -> addToken (next)
                }
            } else {
                addToken (next)
            }
        }

        return happy to sad
    }

    override fun perform (iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (happy, sad) = parseIf (iter)
        if (sm.popBoolean()) {
            sm.execute (happy, terminal)
        } else {
            if (sad != null) {
                sm.execute (sad, terminal)
            }
        }
        return
    }
}

//class If: Builtin(NAME) {
//    companion object {
//        const val NAME = "IF"
//        const val THEN = "THEN"
//        const val ELSE = "ELSE"
//    }
//
//    private fun collect (iter: PeekableIterator<Token>, sm: ForthMachine): Pair<List<Token>, List<Token>?> {
//        val list = mutableListOf<Token> ()
//        var embedded = 0
//
//        while (true) {
//            if (! iter.hasNext ()) {
//                throw IllegalStateException ("Unterminated IF.")
//            }
//            val peek = iter.peek ()
//            if (peek is Token.Word) {
//                when (peek.word) {
//                    NAME -> {
//                        embedded ++
//                        list.add (iter.next  ())
//                    }
//                    ELSE -> {
//                        if (embedded > 0) {
//                            list.add (iter.next ())
//                        } else {
//                            iter.next ()
//                            return Pair (list, collectElse (iter, sm))
//                        }
//                    }
//                    THEN -> {
//                        if (embedded > 0) {
//                            embedded --
//                            list.add (iter.next ())
//                        } else {
//                            iter.next ()
//                            return Pair (list, null)
//                        }
//                    }
//                    else -> list.add (iter.next ())
//                }
//            } else {
//                list.add (iter.next ())
//            }
//        }
//    }
//
//    private fun collectElse (iter: PeekableIterator<Token>, sm: ForthMachine): List<Token> {
//        val list = mutableListOf<Token> ()
//        var embedded = 0
//
//        while (true) {
//            if (! iter.hasNext ()) {
//                throw IllegalStateException("Unterminated IF/ELSE.")
//            }
//            val peek = iter.peek ()
//            if (peek is Token.Word) {
//                when (peek.word) {
//                    NAME -> {
//                        embedded ++
//                        list.add (iter.next ())
//                    }
//                    ELSE -> {
//                        if (embedded == 0) {
//                            throw IllegalStateException ("Second ELSE in IF encountered")
//                        } else {
//                            list.add (iter.next ())
//                        }
//                    }
//                    THEN -> {
//                        if (embedded > 0) {
//                            embedded --
//                            list.add (iter.next ())
//                        } else {
//                            iter.next ()
//                            return list
//                        }
//                    }
//                    else -> list.add (iter.next ())
//                }
//            } else {
//                list.add (iter.next ())
//            }
//        }
//    }
//
//    override fun perform (iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
//        val (happy, sad) = collect (iter, sm)
//        if (sm.popBoolean()) {
//            sm.execute (happy, terminal)
//        } else {
//            if (sad != null) {
//                sm.execute (sad, terminal)
//            }
//        }
//        return
//    }
//}