package builtin

import Builtin
import ForthMachine
import PeekableIterator
import Token
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs

val MATH_BUILTINS = listOf (
    Abs::class,
    Negate::class,
    Min::class,
    Max::class,
    Add::class,
    Subtract::class,
    Multiply::class,
    Divide::class,
    Mod::class,
    DivMod::class,
    UR::class,
    UL::class
)

val MATH_EXTRAS = listOf<String> (
    ": */ ROT ROT *  SWAP / ;",
    ": */MOD ROT ROT *  SWAP /MOD ;",
    ": % 100 */ ;",
    ": R%  10 */  5 +  10 / ;"
)

class Abs : Builtin (NAME) {
    companion object {
        const val NAME = "ABS"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.stack.push (abs (sm.stack.pop ()))
        return
    }
}

class Negate : Builtin (NAME) {
    companion object {
        const val NAME = "NEGATE"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.stack.push (- sm.stack.pop ())
        return
    }
}

class Min : Builtin (NAME) {
    companion object {
        const val NAME = "MIN"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.stack.push (min (sm.stack.pop (), sm.stack.pop ()))
        return
    }
}

class Max : Builtin (NAME) {
    companion object {
        const val NAME = "MAX"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.stack.push (max (sm.stack.pop (), sm.stack.pop ()))
        return
    }
}

class Mod : Builtin(NAME) {
    companion object {
        const val NAME = "MOD"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.stack.pop (2)
        sm.stack.push (b % a)

    }
}

class DivMod : Builtin(NAME) {
    companion object {
        const val NAME = "/MOD"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.stack.pop (2)
        sm.stack.push (b % a)
        sm.stack.push (b / a)
    }
}

class Add : Builtin(NAME) {
    companion object {
        const val NAME = "+"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.stack.pop (2)
        sm.stack.push (a + b)
    }

}

class Subtract : Builtin(NAME) {
    companion object {
        const val NAME = "-"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.stack.pop (2)
        sm.stack.push (b - a)
    }
}

class Multiply : Builtin(NAME) {
    companion object {
        const val NAME = "*"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.stack.pop (2)
        sm.stack.push (a * b)
    }
}

class Divide : Builtin(NAME) {
    companion object {
        const val NAME = "/"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (a, b) = sm.stack.pop (2)
        sm.stack.push (b / a)
    }
}

class UR : Builtin (NAME) {
    companion object {
        const val NAME = "U.R"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val width = sm.stack.pop ()
        terminal.append (String.format ("%${width}d", sm.stack.pop ()))
        return
    }
}

class UL : Builtin (NAME) {
    companion object {
        const val NAME = "U.L"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val width = sm.stack.pop ()
        terminal.append (String.format ("%-${width}d", sm.stack.pop ()))
        return
    }
}

// EOF