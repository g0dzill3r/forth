package builtin

import Builtin
import ForthMachine
import PeekableIterator
import Token

val BUILTINS = listOf (
    Spaces::class,
    Emit::class,
    CarriageReturn::class,
    Loop::class,
    Period::class,
    Add::class,
    Subtract::class,
    Multiply::class,
    Divide::class,
    Mod::class,
    DivMod::class,
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
    Forget::class
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

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        println ()
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
            print (' ')
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
        print (c)
        return
    }
}

class Add : Builtin(NAME) {
    companion object {
        const val NAME = "+"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.pop (2)
        sm.push (a + b)
    }

}

class Subtract : Builtin(NAME) {
    companion object {
        const val NAME = "-"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.pop (2)
        sm.push (b - a)
    }
}

class Multiply : Builtin(NAME) {
    companion object {
        const val NAME = "*"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.pop (2)
        sm.push (a * b)
    }
}

class Divide : Builtin(NAME) {
    companion object {
        const val NAME = "/"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.pop (2)
        sm.push (b / a)
    }
}

class Period: Builtin(NAME) {
    companion object {
        const val NAME = "."
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        print ("${sm.pop ()} ")
    }
}

class SafePeriod: Builtin(NAME) {
    companion object {
        const val NAME = ".S"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        if (sm.stack.isNotEmpty()) {
            print("${sm.stack.joinToString(" ")} ")
        } else {
            print ("EMPTY")
        }
    }
}

class Loop: Builtin(NAME) {
    companion object {
        const val NAME= "DO"
        const val LOOP = "LOOP"
    }

    override fun perform (iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val ops = mutableListOf<Token> ()
        val start = sm.pop()
        val end = sm.pop()
        var foundLoop = false
        while (iter.hasNext()) {
            val next = iter.next ()
            if (next is Token.Word && next.word == LOOP) {
                foundLoop = true
                break
            }
            ops.add (next)
        }
        if (! foundLoop) {
            throw IllegalStateException("Missing LOOP instruction.")
        }
        for (i in start until end) {
            sm.execute (ops, terminal)
        }
        return
    }
}

class Mod : Builtin(NAME) {
    companion object {
        const val NAME = "MOD"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.pop (2)
        sm.push (b % a)

    }
}

class DivMod : Builtin(NAME) {
    companion object {
        const val NAME = "/MOD"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.pop (2)
        sm.push (b % a)
        sm.push (b / a)
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
        sm.dictionary.forget (iter.next () as String)
    }
}

// EOF