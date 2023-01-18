/**
 * Symbols for boolean values.
 */

object ForthBoolean
{
    const val TRUE = -1
    const val FALSE = 0

    fun isTrue (i: Int) = i != FALSE
    fun isFalse (i: Int) = i == FALSE
}

// EOF