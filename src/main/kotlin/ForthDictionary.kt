
class ForthDictionary {
    val list = mutableListOf<Pair<String, Invokable>> ()

    val ops: MutableMap<String, MutableList<Invokable>> = mutableMapOf ()

    fun forget (op: String) {
        val index = find (op)
        if (index != null) {
            while (list.size != index) {
                list.removeAt (index)
            }
        } else {
            throw IllegalArgumentException ("No entry found for $op")
        }
    }

    fun find (op: String): Int? {
        for (i in list.indices.reversed()) {
            val el = list[i]
            if (el.first == op) {
                return i
            }
        }
        return null
    }

    fun get (op: String): Invokable? {
        for (i in list.indices.reversed()) {
            val el = list[i]
            if (el.first == op) {
                return el.second
            }
        }
        return null
    }

    fun add (op: String, binding: Invokable) {
        list.add (op to binding)
        return
    }

    fun add (builtin: Builtin) = add (builtin.name, builtin)
}


// EOF