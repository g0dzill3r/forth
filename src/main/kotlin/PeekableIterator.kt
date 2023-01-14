fun <T> Iterator<T>.peekable () : PeekableIterator<T> = PeekableIterator<T> (this)

class PeekableIterator<T> (val wrapped: Iterator<T>): Iterator<T> {
    private var saved: MutableList<T> = mutableListOf ()

    fun hasNext (index: Int = 0): Boolean {
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

    fun peek (index: Int = 0): T {
        while (saved.size < index + 1) {
            saved.add (wrapped.next ())
        }
        return saved.get (index)
    }

    override fun next(): T {
        return if (saved.isNotEmpty()) {
            saved.removeAt(0)
        } else {
            wrapped.next()
        }
    }
}