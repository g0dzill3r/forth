package builtin

import Builtin
import ForthMachine
import PeekableIterator
import Token

val RETSTACK_BUILTINS = listOf (
    PushRetStack::class,
    PopRetStack::class,
    CopyRetStack::class,
    RetStackPeriod::class
)

val RETSTACK_EXTRAS = listOf<String> ()

class PushRetStack : Builtin (NAME) {
    companion object {
        const val NAME = ">R"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.returnStack.push (sm.stack.pop ())
        return
    }
}

class PopRetStack : Builtin (NAME) {
    companion object {
        const val NAME = "R>"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.stack.push (sm.returnStack.pop ())
        return
    }
}

class CopyRetStack : Builtin (NAME) {
    companion object {
        const val NAME = "@R"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.stack.push (sm.returnStack.peek ())
        return
    }
}

class RetStackPeriod: Builtin(NAME) {
    companion object {
        const val NAME = ".R"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        terminal.append (sm.returnStack.dump (sm.base))
        return
    }
}


// EOF