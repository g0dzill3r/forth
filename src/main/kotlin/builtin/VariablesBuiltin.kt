package builtin

import Builtin
import ForthMachine
import Invokable
import PeekableIterator
import Token

val VARIABLES_BUILTINS = listOf (
    Variable::class,
    Store::class,
    Fetch::class,
    Constant::class
)

val VARIABLES_EXTRAS = listOf<String> (
    ": ? @ . ;",
    ": !+ DUP @ 1 + SWAP ! ;"
)

class Variable : Builtin (NAME) {
    companion object {
        const val NAME = "VARIABLE"
    }

    override fun perform (iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val word = iter.next ()
        if (word !is Token.Word) {
            error ("Not a word: $word")
        }
        val addr = sm.variables.map (word.word)
        // NOT STORED
        return
    }
}

class Store : Builtin (NAME) {
    companion object {
        const val NAME = "!"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val (word, value) = sm.stack.pop (2)
        sm.variables.store (word, value)
        return
    }
}

class Fetch : Builtin (NAME) {
    companion object {
        const val NAME = "@"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val addr = sm.stack.pop ()
        val value = sm.variables.fetch (addr) !!
        sm.stack.push (value)
        return
    }
}

class Constant : Builtin (NAME) {
    companion object {
        const val NAME = "CONSTANT"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        val value = sm.stack.pop ()
        val word = iter.next ()
        if (word !is Token.Word) {
            error ("Not a word: $word")
        }
        sm.dictionary.add (word.word, object: Invokable {
            override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
                sm.stack.push (value)
            }
        })
        return
    }
}

// EOF