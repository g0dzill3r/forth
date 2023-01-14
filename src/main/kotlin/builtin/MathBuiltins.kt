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
    Max::class
)

val MATH_EXTRAS = listOf<String> (

)

class Abs : Builtin (NAME) {
    companion object {
        const val NAME = "ABS"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.push (abs (sm.pop ()))
        return
    }
}

class Negate : Builtin (NAME) {
    companion object {
        const val NAME = "NEGATE"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.push (- sm.pop ())
        return
    }
}

class Min : Builtin (NAME) {
    companion object {
        const val NAME = "MIN"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.push (min (sm.pop (), sm.pop ()))
        return
    }
}

class Max : Builtin (NAME) {
    companion object {
        const val NAME = "MAX"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.push (max (sm.pop (), sm.pop ()))
        return
    }
}


// EOF