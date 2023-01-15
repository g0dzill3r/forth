/**
 *
 */

sealed class Token (val loc: Source.Location) {
    abstract fun render (): String

    class Colon (loc: Source.Location) : Token (loc) {
        override fun render(): String = ":"
        override fun toString(): String = "Colon"
    }
    class Semicolon (loc: Source.Location): Token (loc) {
        override fun render(): String = ";"
        override fun toString(): String = "Semicolon"
    }
    class IntValue (val value: Int, loc: Source.Location): Token (loc) {
        override fun render(): String = "$value"
        override fun toString(): String = "IntValue($value)"
    }
    class Word (val word: String, loc: Source.Location): Token (loc) {
        override fun render(): String = word
        override fun toString(): String = "Word($word)"
    }
    class QuotedString (val string: String, loc: Source.Location): Token (loc) {
        override fun render(): String = ".\"$string\""
        override fun toString(): String = "QuotedString(\"$string\")"
    }
    class Comment (val comment: String, loc: Source.Location): Token (loc) {
        override fun render(): String = "($comment)"
        override fun toString (): String = "Comment($comment)"
    }
}

object ForthLexer {
    /**
     *
     */

    private fun readComment(iter: Source): Token.Comment {
        val comment = StringBuffer()
        while (iter.hasNext() && iter.peek() != ')') {
            comment.append(iter.next())
        }
        return if (!iter.hasNext()) {
            throw IllegalArgumentException("Unclosed comment.")
        } else {
            iter.next()
            Token.Comment(comment.toString(), iter.location)
        }
    }

    /**
     *
     */

    private fun readQuotedString(iter: Source): Token.QuotedString {
        val buf = StringBuffer()
        iter.next()
        while (iter.hasNext() && iter.peek() != '"') {
            buf.append(iter.next())
        }
        return if (!iter.hasNext()) {
            throw IllegalArgumentException("Unclosed quoted string.")
        } else {
            iter.next()
            Token.QuotedString(buf.toString(), iter.location)
        }
    }

    /**
     *
     */

    fun lexer(input: String): Sequence<Token> {
        return sequence {
            val buf = StringBuffer()
            var inWord = false
            val iter = Source(input)

            while (iter.hasNext()) {
                val c = iter.next()
                if (inWord) {
                    if (c == ' ' || c == '\n') {
                        val token = buf.toString()
                        if (token.isNumber) {
                            yield(Token.IntValue(token.toInt(), iter.location))
                        } else {
                            yield(Token.Word(token, iter.location))
                        }
                        buf.setLength(0)
                        inWord = false
                    } else {
                        buf.append(c)
                    }
                } else {
                    when (c) {
                        ' ' -> Unit
                        ':' -> yield(Token.Colon(iter.location))
                        ';' -> yield(Token.Semicolon(iter.location))
                        '(' -> yield(readComment(iter))
                        ')' -> throw IllegalArgumentException("Unexpected close comment")
                        '.' -> {
                            if (iter.hasNext() && iter.peek() == '\"') {
                                yield(readQuotedString(iter))
                            } else {
                                buf.append(c)
                                inWord = true
                            }
                        }
                        '\n' -> Unit
                        else -> {
                            buf.append(c)
                            inWord = true
                        }
                    }
                }
            }
            if (buf.isNotEmpty()) {
                val token = buf.toString()
                yield(
                    if (token.isNumber) {
                        Token.IntValue(token.toInt(), iter.location)
                    } else {
                        Token.Word(token, iter.location)
                    }
                )
            }
        }
    }
}

// EOF