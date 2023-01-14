import java.util.*
import kotlin.reflect.KClass

fun interpreter (prompt: String = "> ", func: (String) -> Boolean) {
    while (true) {
        try {
            print (prompt)
            val input = readln()
            if (! func (input)) {
                break
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return
}

fun stringBuilder (func: StringBuffer.() -> Unit): String {
    return StringBuffer ().apply {
        func ()
    }.toString ()
}


fun <T: Invokable> instances (types: List<KClass<out T>>): List<T> {
    return types.map {
        it.java.getConstructor().newInstance()
    }
}

val String.isNumber: Boolean
    get () {
        return if (length > 1 && startsWith ("-")) {
            substring (1, length).isDigits
        } else {
            isDigits
        }
    }

val String.isDigits: Boolean
    get () {
        forEach { c ->
            if (!c.isDigit()) {
                return false
            }
        }
        return true
    }