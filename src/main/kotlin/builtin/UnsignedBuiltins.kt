package builtin

import Builtin
import ForthMachine
import PeekableIterator
import Token

val UNSIGNED_BUILTINS = listOf (
    UnsignedPeriod::class,
    UnsignedLessThan::class,
    UnsignedMod::class,
    UnsignedMultiply::class,
    Hex::class,
    Octal::class,
    Decimal::class,
    Binary::class
)

val UNSIGNED_EXTRAS = listOf<String> ()

class UnsignedPeriod : Builtin (NAME) {
    companion object {
        const val NAME = "U."
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {

    }
}

class UnsignedMultiply : Builtin (NAME) {
    companion object {
        const val NAME = "UM*"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {

    }
}

class UnsignedMod : Builtin (NAME) {
    companion object {
        const val NAME = "UM/MOD"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {

    }
}

class UnsignedLessThan : Builtin (NAME) {
    companion object {
        const val NAME = "U<"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {

    }
}



class Hex : Builtin (NAME) {
    companion object {
        const val NAME = "HEX"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.base = 16
        return
    }
}


class Binary : Builtin (NAME) {
    companion object {
        const val NAME = "BINARY"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.base = 2
        return
    }
}

class Octal : Builtin (NAME) {
    companion object {
        const val NAME = "OCTAL"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.base = 8
        return
    }
}

class Decimal : Builtin (NAME) {
    companion object {
        const val NAME = "DECIMAL"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.base = 10
        return
    }
}



// EOF