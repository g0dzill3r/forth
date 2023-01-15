package builtin

import Builtin
import ForthMachine
import PeekableIterator
import Token

val BUILTINS = listOf (
    Spaces::class,
    Emit::class,
    CarriageReturn::class,
    Do::class,
    Period::class,
    Swap::class,
    Dup::class,
    Rot::class,
    Over::class,
    Drop::class,
    Clear::class,
    SafePeriod::class,
    TwoSwap::class,
    TwoDup::class,
    TwoOver::class,
    TwoDrop::class,
    Forget::class,
    Begin::class
)

val BUILTIN_EXTRAS = listOf (
    ": 1+ 1 + ;",
    ": 1- 1 - ;",
    ": 2+ 2 + ;",
    ": 2- 2 - ;",
    ": 2* 2 * ;",
    ": 2/ 2 / ;",
)

class UserDefined (name: String, val args: List<Token>) : Builtin(name) {
    override fun perform (iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.execute (args, terminal)
        if (sm.returnStack.isNotEmpty()) {
            sm.returnStack.clear ()
            throw IllegalStateException ("Return stack contains ${sm.returnStack.size} items")
        }
        return
    }

    override fun toString(): String {
        return StringBuffer ().apply {
            append (": $name   ")
            args.forEach {
                append ("${it.render ()} ")
            }
            append (";")
        }.toString ()
    }
}

class CarriageReturn : Builtin(NAME) {
    companion object {
        const val NAME = "CR"
    }

    override fun perform (iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        terminal.append ("\n")
        return
    }
}

class Spaces : Builtin(NAME) {
    companion object {
        const val NAME = "SPACES"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val times = sm.pop ()
        repeat (times) {
            terminal.append (' ')
        }
    }
}

class Emit : Builtin(NAME) {
    companion object {
        const val NAME = "EMIT"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val code = sm.pop ()
        val c = Char (code)
        terminal.append (c)
        return
    }
}

class Period: Builtin(NAME) {
    companion object {
        const val NAME = "."
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        terminal.append ("${sm.pop ()} ")
    }
}

class SafePeriod: Builtin(NAME) {
    companion object {
        const val NAME = ".S"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        if (sm.stack.isNotEmpty()) {
            terminal.append ("<${sm.stack.size}> ${sm.stack.joinToString(" ")} ")
        } else {
            terminal.append ("<${sm.stack.size}> EMPTY")
        }
    }
}

class Do: Builtin(NAME) {
    companion object {
        const val NAME= "DO"
        const val LOOP = "LOOP"
        const val LOOP_PLUS = "LOOP+"
    }

    private fun collect (iter: PeekableIterator<Token>) : Pair<List<Token>, Boolean> {
        val ops = mutableListOf<Token> ()
        var embedded = 0

        while (true) {
            if (! iter.hasNext ()) {
                throw IllegalStateException("Missing LOOP instruction.")
            }
            val next = iter.next ()
            if (next is Token.Word) {
                if (next.word == LOOP || next.word == LOOP_PLUS) {
                    if (embedded > 0) {
                        embedded --
                    } else {
                        return Pair (ops, next.word == LOOP_PLUS)
                    }
                } else if (next.word == NAME) {
                    embedded ++
                }
            }
            ops.add (next)
        }

        // NOT REACHED
    }

    private fun update (index: Int, depth: Int, list: List<Token>): List<Token> {
        val word = Char ('I'.code + depth - 1).toString ()
        return list.map {
                if (it is Token.Word && it.word == word) {
                    Token.IntValue (index, it.loc)
                } else {
                    it
                }
        }
    }

    var depth = 0

    override fun perform (iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        try {
            depth ++
            val start = sm.pop()
            val end = sm.pop()
            val (loop, isPlus) = collect(iter)

            var i = start
            while (i < end) {
                sm.execute (update (i, depth, loop), terminal)
                if (isPlus) {
                    i += sm.pop ()
                } else {
                    i ++
                }
            }
        }
        finally {
            depth --
        }
        return
    }
}

class Swap : Builtin(NAME) {
    companion object {
        const val NAME = "SWAP"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.pop (2)
        sm.push (a, b)
        return
    }

}

class Dup: Builtin(NAME) {
    companion object {
        const val NAME = "DUP"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.push (sm.peek ())
        return
    }
}

class Over: Builtin(NAME) {
    companion object {
        const val NAME = "OVER"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.pop (2)
        sm.push (b, a, b)
        return
    }
}

class Rot: Builtin(NAME) {
    companion object {
        const val NAME = "ROT"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b, c) = sm.pop (3)
        sm.push (b, a, c)
        return
    }
}

class Drop: Builtin(NAME) {
    companion object {
        const val NAME = "DROP"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.pop ()
        return
    }
}

class Clear: Builtin(NAME) {
    companion object {
        const val NAME = "CLEAR"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.stack.clear ()
        return
    }
}

class TwoSwap : Builtin(NAME) {
    companion object {
        const val NAME = "2SWAP"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b, c, d) = sm.pop (4)
        sm.push (b, a, d, c)
        return
    }
}

class TwoDup : Builtin(NAME) {
    companion object {
        const val NAME = "2DUP"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.pop (2)
        sm.push (b, a, b, a)
        return
    }
}

class TwoOver : Builtin(NAME) {
    companion object {
        const val NAME = "2OVER"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b, c, d) = sm.pop (4)
        sm.push (d, c, b, a, d, c)
        return
    }
}

class TwoDrop : Builtin(NAME) {
    companion object {
        const val NAME = "2DROP"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.pop (2)
        return
    }
}

class Forget : Builtin(NAME) {
    companion object {
        const val NAME = "FORGET"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val word = iter.next () as Token.Word
        sm.dictionary.forget (word.word)
    }
}
class Begin : Builtin (NAME) {
    companion object {
        const val NAME = "BEGIN"
        const val UNTIL = "UNTIL"
    }

    private fun collect (iter: PeekableIterator<Token>): List<Token> {
        val list = mutableListOf<Token> ()
        while (true) {
            if (! iter.hasNext ()) {
                throw IllegalStateException ("Unterminated $NAME loop.")
            }
            val next = iter.next ()
            if (next is Token.Word && next.word == UNTIL) {
                break
            }
            list.add (next)
        }
        return list
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val ops = collect (iter)
        while (true) {
            sm.execute (ops, terminal)
            if (! sm.popBoolean()) {
                break
            }
        }
        return
    }
}

// EOF