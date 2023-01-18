/**
 *
 */

class Source (iter: Iterator<Char>, val file: String = "input"): Iterator<Char> {
    constructor (input: String, file: String = "input"): this (input.iterator ())

    private val wrapped = iter
    private var saved: MutableList<Char> = mutableListOf ()

    var row: Int = 0
        private set

    var column: Int = 0
        private set

    data class Location (val file: String, val row: Int, val column: Int) {
        override fun toString (): String = "$file($row:$column)"
    }
    val location: Location
        get () = Location (file, row, column)

    fun hasNext (index: Int): Boolean {
        while (saved.size < index + 1) {
            if (wrapped.hasNext ()) {
                saved.add (wrapped.next ())
            } else {
                return false
            }
        }
        return true
    }

    override fun hasNext(): Boolean {
        return if (saved.isNotEmpty ()) {
            true
        } else {
            wrapped.hasNext ()
        }
    }

    fun peek (index: Int = 0): Char {
        while (saved.size < index + 1) {
            saved.add (wrapped.next ())
        }
        return saved.get (index)
    }

    private fun update (c: Char) {
        if (c == '\n') {
            row ++
            column = 0
        } else {
            column ++
        }
    }

    override fun next(): Char {
        return if (saved.isNotEmpty()) {
            saved.removeAt (0)
        } else {
            val next = wrapped.next ()
            update (next)
            next
        }
    }
}

fun main() {
    val input = """
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras gravida urna a suscipit maximus. Sed eu 
        sollicitudin tellus. Praesent luctus massa magna, ac auctor lacus elementum sit amet. Nullam lobortis 
        mollis risus vitae efficitur. Nulla commodo et lorem et posuere. Donec varius orci enim, vel mollis enim 
        pharetra ac. Nulla facilisi. Curabitur dictum massa quis lacus consequat efficitur. Nulla eget blandit sem, 
        in vestibulum nisl. Aliquam erat volutpat. Vivamus egestas porta tortor, ut congue risus porttitor id. 
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et molestie arcu. Sed sollicitudin, tortor nec 
        hendrerit feugiat, velit nisi aliquam mauris, et pellentesque ante tortor sit amet tortor. In orci turpis, 
        pretium ut ultricies eget, efficitur ac dui. Quisque hendrerit mi quis risus vestibulum, finibus elementum nisl convallis.
    """.trimIndent()

    val source = Source (input)
    while (source.hasNext ()) {
        val next = source.next ()
        if (next.isUpperCase()) {
            println ("Found a '$next' at ${source.location}")
        }
    }
    return
}

// EOF