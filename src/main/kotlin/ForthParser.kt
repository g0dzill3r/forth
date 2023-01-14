import java.lang.IllegalStateException

sealed class Statement (val list: List<Token>) {
    class Declaration (list: List<Token>) : Statement (list) {
        override fun toString(): String {
            return stringBuilder {
                append (": ")
                list.forEach {
                    append (it.render ())
                    append (' ')
                }
                append (";")
            }
        }
    }
    class Expression (list: List<Token>) : Statement (list) {
        override fun toString(): String {
            return stringBuilder {
                list.forEach {
                    append (it.render ())
                    append (' ')
                }
            }
        }
    }
}

/**
 *
 */

object ForthParser {
    fun parse (input: String): Sequence<Statement> {
        return sequence {
            val iter = ForthLexer.lexer (input).iterator().peekable()
            while (iter.hasNext ()) {
                yield (if (iter.peek () is Token.Colon) {
                    parseDeclaration (iter)
                } else {
                    parseExpression (iter)
                })
            }
        }
    }

    private fun parseDeclaration (iter: PeekableIterator<Token>): Statement.Declaration {
        val list = buildList {
            iter.next ()
            while (iter.hasNext () && iter.peek () !is Token.Semicolon) {
                add (iter.next ())
            }
            if (iter.hasNext ()) {
                iter.next ()
            } else {
                throw IllegalStateException ("Missing ';' terminator.")
            }
        }
        return Statement.Declaration(list)
    }


    private fun parseExpression (iter: PeekableIterator<Token>): Statement.Expression {
        val list = buildList {
            while (iter.hasNext () && iter.peek () !is Token.Colon) {
                add (iter.next ())
            }
        }
        return Statement.Expression (list)
    }
}

// EOF