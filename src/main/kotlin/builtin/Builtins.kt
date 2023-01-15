package builtin

import Builtin
import ForthMachine
import PeekableIterator
import Token

val BUILTINS = listOf (
    Spaces::class,
    Emit::class,
    CarriageReturn::class,
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

// EOF