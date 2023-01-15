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
        sm.returnStack.push (sm.pop ())
        return
    }
}

class PopRetStack : Builtin (NAME) {
    companion object {
        const val NAME = "R>"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.push (sm.returnStack.pop ())
        return
    }
}

class CopyRetStack : Builtin (NAME) {
    companion object {
        const val NAME = "@R"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        sm.push (sm.returnStack.peek ())
        return
    }
}

class RetStackPeriod: Builtin(NAME) {
    companion object {
        const val NAME = ".R"
    }

    override fun perform(iter: PeekableIterator<Token>, sm: ForthMachine, terminal: StringBuffer) {
        if (sm.returnStack.isNotEmpty()) {
            print("${sm.returnStack.joinToString(" ")} ")
        } else {
            print ("EMPTY")
        }
    }
}


// EOF